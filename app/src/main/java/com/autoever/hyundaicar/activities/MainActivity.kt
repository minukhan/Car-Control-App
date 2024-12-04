package com.autoever.hyundaicar.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.models.Car
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        /*val editTextCarName = findViewById<TextView>(R.id.editTextCarName)

        // "가입하기 버튼"
        val textViewComplete = findViewById<TextView>(R.id.textViewComplete)
        textViewComplete.setOnClickListener {
            val car = Car(
                "",editTextCarName.text.toString()
            )
            saveCarData(car)
        }*/

        // View 초기화
        val btnHome = findViewById<LinearLayout>(R.id.btnHome)
        val btnControl = findViewById<LinearLayout>(R.id.btnControl)
        val btnStatus = findViewById<LinearLayout>(R.id.btnStatus)
        val btnMap = findViewById<LinearLayout>(R.id.btnMap)

        // 버튼 동작
        btnHome.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity::class.java)
            startActivity(intent)
        }
        btnControl.setOnClickListener {
            val intent = Intent(this@MainActivity, ControlActivity::class.java)
            startActivity(intent)
        }
        btnStatus.setOnClickListener {
            val intent = Intent(this@MainActivity, StatusActivity::class.java)
            startActivity(intent)
        }
        btnMap.setOnClickListener {
            val intent = Intent(this@MainActivity, MapActivity::class.java)
            startActivity(intent)
        }

    }

    // Firestore에 사용자 데이터 저장
    private fun saveCarData(car: Car) {
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
    }
}