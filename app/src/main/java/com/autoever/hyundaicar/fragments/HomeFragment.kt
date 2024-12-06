package com.autoever.hyundaicar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.models.Car
import com.autoever.hyundaicar.models.User
import com.autoever.hyundaicar.viewmodel.WeatherViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {
    val user = User()
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 뷰 참조
        val tvModel = view.findViewById<TextView>(R.id.tvModel)
        val tvDistance = view.findViewById<TextView>(R.id.tvDistance)

        fetchUserInfo()

        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTemperature: TextView = view.findViewById(R.id.tvTemperature)
        val skyImageView = view.findViewById<ImageView>(R.id.SkyView)

        // 날씨 데이터 가져오기
        weatherViewModel.fetchWeatherData()

        lifecycleScope.launch {
            weatherViewModel.weatherData.collect { weatherItems ->
                // 데이터 필터링 및 처리
                val temperatureData = weatherItems.firstOrNull { it.category == "TMP" } // 온도
                val rawDate = weatherItems.firstOrNull()?.fcstDate ?: "Loading..."

                // 하늘 상태 코드 (SKY)
                val skyData = weatherItems.firstOrNull { it.category == "SKY" }
                val skyCode = skyData?.fcstValue ?: "Loading..."

                // 하늘 상태에 따라 이미지 변경
                val skyImageResource = when (skyCode) {
                    "1" -> R.drawable.sunny_icon  // 맑음
                    "3" -> R.drawable.cloudy_icon // 구름 많음
                    "4" -> R.drawable.cloud_icon // 흐림
                    else -> R.drawable.sunny_icon // 기본값
                }
                skyImageView.setImageResource(skyImageResource)

                // 날짜 포맷팅 및 요일 추가
                val formattedDate = if (rawDate.length == 8) {
                    val year = rawDate.substring(0, 4) // 연도
                    val month = rawDate.substring(4, 6) // 월
                    val day = rawDate.substring(6, 8) // 일

                    // 날짜 문자열을 Date 객체로 변환
                    val dateString = "$year-$month-$day" // YYYY-MM-DD 형식
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date = dateFormat.parse(dateString)

                    // 요일 추출
                    val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // 요일
                    var dayOfWeek = dayOfWeekFormat.format(date)

                    if(dayOfWeek == "Friday"){
                        dayOfWeek = "금요일"
                    }

                    "${year}년 ${month}월 ${day}일 $dayOfWeek"  // 날짜 + 요일
                } else {
                    "Loading..."
                }

                // 뷰에 데이터 업데이트
                tvTemperature.text = temperatureData?.fcstValue?.let { "$it°C" } ?: "Loading..."
                tvDate.text = formattedDate
            }
        }

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
