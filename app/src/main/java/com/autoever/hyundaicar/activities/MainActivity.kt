package com.autoever.hyundaicar.activities
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.models.Car
import com.google.firebase.firestore.FirebaseFirestore
import com.kakao.vectormap.KakaoMapSdk
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mapImageView: ImageView = findViewById(R.id.mapImageView) // ImageView의 ID를 사용하여 참조
        mapImageView.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        val controlImageView: ImageView = findViewById(R.id.controlImageView) // ImageView의 ID를 사용하여 참조
        controlImageView.setOnClickListener {
            val intent = Intent(this, ControlActivity::class.java)
            startActivity(intent)
        }


        /*try {
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.signingInfo?.apkContentsSigners
            } else {
                info.signatures
            }

            if (signatures != null) {
                for (signature in signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val keyHash = String(Base64.encode(md.digest(), Base64.DEFAULT))
                    Log.d("KeyHash", keyHash)
                    Toast.makeText(this, "KeyHash: $keyHash", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e("KeyHash", "Unable to get KeyHash", e)
        }*/
    }

        /*val editTextCarName = findViewById<TextView>(R.id.editTextCarName)

        // "가입하기 버튼"
        val textViewComplete = findViewById<TextView>(R.id.textViewComplete)
        textViewComplete.setOnClickListener {
            val car = Car(
                "",editTextCarName.text.toString()
            )
            saveCarData(car)
        }*/


    // Firestore에 사용자 데이터 저장
    /*private fun saveCarData(car: Car) {
        val firestore = FirebaseFirestore.getInstance()

        // 사용자 UID를 Firestore 문서 ID로 사용하여 저장
        firestore.collection("cars")
            .add(car)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "차량 등록 완료",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "차량 등록 실패",Toast.LENGTH_SHORT).show()
            }
    }*/
}