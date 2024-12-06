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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_status, container, false)

        // 뷰 참조
        val tvModel = view.findViewById<TextView>(R.id.tvModel)
        val tvDistance = view.findViewById<TextView>(R.id.tvDistance)


        fetchUserInfo()
        return view
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