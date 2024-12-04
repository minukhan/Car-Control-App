package com.autoever.hyundaicar.activities

import android.os.Bundle
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editTextCarName = findViewById<TextView>(R.id.editTextCarName)

        // "가입하기 버튼"
        val textViewComplete = findViewById<TextView>(R.id.textViewComplete)
        textViewComplete.setOnClickListener {
            val car = Car(
                "",editTextCarName.text.toString()
            )
            saveCarData(car)
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