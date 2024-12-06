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
import com.autoever.hyundaicar.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

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

        fetchUserInfo()

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
        }

        btnUnlock.setOnClickListener {
            car.isLocked = false
            showToast("문이 열렸습니다.")
        }

        btnStart.setOnClickListener {
            if (!car.isStarted) {
                car.isStarted = true
                showToast("차량 시동이 켜졌습니다.")
            } else {
                showToast("시동이 이미 켜져 있습니다.")
            }
        }

        btnStop.setOnClickListener {
            if (car.isStarted) {
                car.isStarted = false
                showToast("차량 시동이 꺼졌습니다.")
            } else {
                showToast("시동이 이미 꺼져 있습니다.")
            }
        }

        btnEmergency.setOnClickListener {
            if (!car.emergencyLight) {
                car.emergencyLight = false
                showToast("비상등이 켜졌습니다.")
            } else {
                showToast("비상등이 이미 켜져 있습니다.")
            }
        }

        btnEmergencyStop.setOnClickListener {
            if (car.emergencyLight) {
                car.emergencyLight = false
                showToast("경적이 꺼졌습니다.")
            } else {
                showToast("경적을 울립니다.")
            }
        }

        return view
    }

    private fun updateUI() {
        tvModel.text = car.name
        tvDistance.text = "남은 거리: ${car.distanceToEmpty}km"
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Firestore에서 사용자 데이터를 가져옴
                    val user = document.toObject(User::class.java)
                    if (user != null && user.cars.isNotEmpty()) {
                        // 첫 번째 차량 정보 가져오기 (예: cars 리스트의 첫 번째 항목)
                        val car = user.cars[0]
                        updateCarInfo(car)
                    } else {
                        Toast.makeText(requireContext(), "등록된 차량이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "데이터를 가져오는 중 오류 발생: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateCarInfo(car: Car) {
        val tvModel = view?.findViewById<TextView>(R.id.tvModel)
        val tvDistance = view?.findViewById<TextView>(R.id.tvDistance)

        tvModel?.text = car.name
        tvDistance?.text = "${car.distanceToEmpty} km" // 남은 주행 거리 표시
    }
}