package com.autoever.hyundaicar.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.autoever.hyundaicar.R
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // View 초기화
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPW = findViewById<EditText>(R.id.editTextPassword)
        val textViewComplete = findViewById<TextView>(R.id.btnConfirm)
        val btnSignUp = findViewById<TextView>(R.id.btnSignUp)

        textViewComplete.setOnClickListener {
            login(editTextEmail.text.toString(), editTextPW.text.toString())
        }

        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    fun login(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    // 로그인 실패
                    Toast.makeText(baseContext, "로그인 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
