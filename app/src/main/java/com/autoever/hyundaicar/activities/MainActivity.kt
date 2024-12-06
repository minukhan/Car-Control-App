package com.autoever.hyundaicar.activities

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.models.Car
import com.autoever.hyundaicar.models.User
import com.autoever.hyundaicar.viewmodel.WeatherViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // View 초기화
        val tvModel = findViewById<TextView>(R.id.tvModel)
        val tvDistance = findViewById<TextView>(R.id.tvDistance)
        val imageViewChangeCar = findViewById<ImageView>(R.id.ivIcon1)
        val imageViewMyPage = findViewById<ImageView>(R.id.ivIcon2)
        val imageButtonLogout = findViewById<ImageView>(R.id.imageButtonLogout)

        // 로그아웃 버튼 동작
        imageButtonLogout.setOnClickListener {
            // Firebase 인증 로그아웃
            FirebaseAuth.getInstance().signOut()

            // 인트로 화면으로 이동
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 이전 액티비티 제거
            startActivity(intent)
        }

        // 내 차량 정보 불러오기
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val currentUserId = currentUser.uid
            getUser(currentUserId) { user ->
                if (user != null && user.cars.isNotEmpty()) {
                    // 첫 번째 차량 정보 가져오기
                    val car = user.cars[0]

                    // 차량 모델명과 남은 거리 표시
                    tvModel.text = car.name
                    tvDistance.text = "${car.distanceToEmpty}km"
                } else {
                    // 차량 정보가 없을 경우 처리
                    println("No car information found.")
                }
            }
        }

        // NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

    }

    // Firestore에 사용자 데이터 저장
    private fun saveCarData(car: Car) {
        val firestore = FirebaseFirestore.getInstance()

        // 사용자 UID를 Firestore 문서 ID로 사용하여 저장
        firestore.collection("cars")
            .add(car)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "차량 등록 완료", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "차량 등록 실패", Toast.LENGTH_SHORT).show()
            }
    }

    fun getUser(userId: String, callback: (User?) -> Unit) {
        db.collection("users")
            .document(userId) // 사용자 ID로 문서 가져오기
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    callback(user) // 변환한 User 객체를 callback으로 전달
                } else {
                    callback(null) // 해당하는 문서가 없을 경우 null 전달
                }
            }
            .addOnFailureListener { exception ->
                // 에러 처리
                println("Error getting document: $exception")
                callback(null) // 에러 발생 시 null 전달
            }
    }
}
