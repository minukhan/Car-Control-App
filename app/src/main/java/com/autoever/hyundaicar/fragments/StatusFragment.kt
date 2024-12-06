package com.autoever.hyundaicar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.models.Car
import com.autoever.hyundaicar.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StatusFragment: Fragment() {
    private lateinit var tvDistance: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvEngineStatus: TextView
    private lateinit var tvDoorStatus: TextView
    private lateinit var tvEmergencyStatus: TextView
    private lateinit var tvLocation: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_status, container, false)

        initializeViews(view)
        fetchUserInfo()
        return view
    }

    private fun initializeViews(view: View) {
        tvDistance = view.findViewById(R.id.tvDistance)
        tvTemperature = view.findViewById(R.id.tvTemperature)
        tvEngineStatus = view.findViewById(R.id.tvEngineStatus)
        tvDoorStatus = view.findViewById(R.id.tvDoorStatus)
        tvEmergencyStatus = view.findViewById(R.id.tvEmergencyStatus)
    }

    private fun fetchUserInfo() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "사용자가 로그인되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        db.collection("users")
            .document(userId)
            .addSnapshotListener { document, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "데이터를 가져오는 중 오류 발생: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (document != null) {
                    val user = document.toObject(User::class.java)
                    if (user != null && user.cars.isNotEmpty()) {
                        updateCarInfo(user.cars[0])
                    } else {
                        Toast.makeText(requireContext(), "등록된 차량이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateCarInfo(car: Car) {
        // 주행 가능 거리
        tvDistance.text = "${car.distanceToEmpty}km"

        // 실내 온도
        tvTemperature.text = "${car.temperature}°C"

        // 시동 상태
        tvEngineStatus.text = if (car.isStarted) "켜짐" else "꺼짐"

        // 문 잠금 상태
        tvDoorStatus.text = if (car.isLocked) "잠김" else "열림"

        // 비상등 상태
        tvEmergencyStatus.text = if (car.emergencyLight) "ON" else "OFF"
    }
}