package com.autoever.hyundaicar.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.models.Car
import com.autoever.hyundaicar.models.Location

class ControlActivity : AppCompatActivity() {

    // Car 객체 초기화
    private var car = Car(
        id = "001",
        name = "G70",
        image = "car_image",
        isStarted = false,
        isLocked = true,
        temperature = 22.0,
        distanceToEmpty = 300,
        location = Location(37.5665, 126.9780)
    )

    // UI 요소 초기화
    private lateinit var ivCarStatus: ImageView
    private lateinit var tvModel: TextView
    private lateinit var tvDistance: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_control)

        // 뷰 바인딩
        ivCarStatus = findViewById(R.id.ivCarStatus)
        tvModel = findViewById(R.id.tvModel)
        tvDistance = findViewById(R.id.tvDistance)

        val btnLock: ImageButton = findViewById(R.id.btnLock)
        val btnUnlock: ImageButton = findViewById(R.id.btnUnlock)
        val btnStart: ImageButton = findViewById(R.id.btnStart)
        val btnStop: ImageButton = findViewById(R.id.btnStop)
        val btnEmergency: ImageButton = findViewById(R.id.btnEmergency)
        val btnEmergencyStop: ImageButton = findViewById(R.id.btnEmergencyStop)

        // 화면 초기화
        updateUI()

        // 버튼 클릭 리스너 설정
        btnLock.setOnClickListener {
            car.isLocked = true
            showToast("문이 잠겼습니다.")
            updateUI()
        }

        btnUnlock.setOnClickListener {
            car.isLocked = false
            showToast("문이 열렸습니다.")
            updateUI()
        }

        btnStart.setOnClickListener {
            if (!car.isStarted) {
                car.isStarted = true
                showToast("차량 시동이 켜졌습니다.")
                updateUI()
            } else {
                showToast("시동이 이미 켜져 있습니다.")
            }
        }

        btnStop.setOnClickListener {
            if (car.isStarted) {
                car.isStarted = false
                showToast("차량 시동이 꺼졌습니다.")
                updateUI()
            } else {
                showToast("시동이 이미 꺼져 있습니다.")
            }
        }

        btnEmergency.setOnClickListener {
            if (!car.emergencyLight) {
                car.emergencyLight = false
                showToast("비상등이 켜졌습니다.")
                updateUI()
            } else {
                showToast("비상등이 이미 켜져 있습니다.")
            }
        }

        btnEmergencyStop.setOnClickListener {
            if (car.emergencyLight) {
                car.emergencyLight = false
                showToast("비상등이 꺼졌습니다.")
                updateUI()
            } else {
                showToast("비상등이 이미 꺼져 있습니다.")
            }
        }
    }

    private fun updateUI() {
        // 차량 이름 업데이트
        tvModel.text = car.name

        // 차량 연료 거리 업데이트
        tvDistance.text = "남은 거리: ${car.distanceToEmpty}km"

        // 차량 상태 이미지 업데이트 (예: 잠금 상태)
        ivCarStatus.setImageResource(
            if (car.isLocked) R.drawable.car_image else R.drawable.g90
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

