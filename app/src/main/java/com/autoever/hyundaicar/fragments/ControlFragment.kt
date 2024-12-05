package com.autoever.hyundaicar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.models.Car
import com.autoever.hyundaicar.models.Location

class ControlFragment : Fragment() {

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

    private lateinit var ivCarStatus: ImageView
    private lateinit var tvModel: TextView
    private lateinit var tvDistance: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_control, container, false)

        // 뷰 바인딩
        ivCarStatus = view.findViewById(R.id.ivCarStatus)
        tvModel = view.findViewById(R.id.tvModel)
        tvDistance = view.findViewById(R.id.tvDistance)

        val btnLock: ImageButton = view.findViewById(R.id.btnLock)
        val btnUnlock: ImageButton = view.findViewById(R.id.btnUnlock)
        val btnStart: ImageButton = view.findViewById(R.id.btnStart)
        val btnStop: ImageButton = view.findViewById(R.id.btnStop)
        val btnEmergency: ImageButton = view.findViewById(R.id.btnEmergency)
        val btnEmergencyStop: ImageButton = view.findViewById(R.id.btnEmergencyStop)

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

        return view
    }

    private fun updateUI() {
        tvModel.text = car.name
        tvDistance.text = "남은 거리: ${car.distanceToEmpty}km"
        ivCarStatus.setImageResource(
            if (car.isLocked) R.drawable.car_image else R.drawable.g90
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}