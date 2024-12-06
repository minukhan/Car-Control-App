package com.autoever.hyundaicar.fragments

import android.media.Image
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.models.Car
import com.autoever.hyundaicar.models.User
import com.autoever.hyundaicar.viewmodel.WeatherViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {
    private val weatherViewModel: WeatherViewModel by viewModels()
    private var currentCar: Car? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        fetchUserInfo()
        setupRealtimeUpdates()

        // 날씨 관련 뷰 초기화
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTemperature: TextView = view.findViewById(R.id.tvTemperature)
        val skyImageView = view.findViewById<ImageView>(R.id.SkyView)

        // 차량 상태 뷰 초기화
        val btnLock: ImageButton = view.findViewById(R.id.btnLock)
        val btnStart: ImageButton = view.findViewById(R.id.btnStart)
        val btnUnlock: ImageButton = view.findViewById(R.id.btnUnlock)

        // 버튼 동작
        btnLock.setOnClickListener {
            currentCar?.let { car ->
                updateCarLockStatus(car.id, true)
            }
        }

        btnStart.setOnClickListener {
            currentCar?.let { car ->
                updateCarEngineStatus(car.id, !car.isStarted)  // 현재 상태의 반대로 토글
            }
        }

        btnUnlock.setOnClickListener {
            currentCar?.let { car ->
                updateCarLockStatus(car.id, false)
            }
        }

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

    private fun updateCarLockStatus(carId: String, isLocked: Boolean) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(currentUser)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                user?.let {
                    // 해당 차량 찾기 및 상태 업데이트
                    val updatedCars = user.cars.map { car ->
                        if (car.id == carId) {
                            car.copy(isLocked = isLocked)
                        } else car
                    }

                    // Firestore 업데이트
                    db.collection("users")
                        .document(currentUser)
                        .update("cars", updatedCars)
                        .addOnSuccessListener {
                            // 성공 메시지
                            val message = if (isLocked) "차량이 잠겼습니다." else "차량이 잠금해제되었습니다."
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "상태 업데이트 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    private fun updateCarEngineStatus(carId: String, isStarted: Boolean) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(currentUser)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                user?.let {
                    val updatedCars = user.cars.map { car ->
                        if (car.id == carId) {
                            car.copy(isStarted = isStarted)
                        } else car
                    }

                    db.collection("users")
                        .document(currentUser)
                        .update("cars", updatedCars)
                        .addOnSuccessListener {
                            // 시동 상태에 따라 다른 소리 재생
                            if (isStarted) {
                                playSound(R.raw.engine_start)
                            }
                            val message = if (isStarted) "시동이 켜졌습니다." else "시동이 꺼졌습니다."
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "상태 업데이트 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    // 소리 재생을 위한 함수 추가
    private fun playSound(resourceId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(requireContext(), resourceId).apply {
            setOnCompletionListener { release() }
            start()
        }
    }

    // Fragment 파괴 시 MediaPlayer 정리
    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun updateCarInfo(car: Car) {
        currentCar = car

        val tvModel = view?.findViewById<TextView>(R.id.tvModel)
        val tvDistance = view?.findViewById<TextView>(R.id.tvDistance)
        val btnLock = view?.findViewById<ImageButton>(R.id.btnLock)
        val btnStart = view?.findViewById<ImageButton>(R.id.btnStart)
        val btnUnlock = view?.findViewById<ImageButton>(R.id.btnUnlock)

        tvModel?.text = car.name
        tvDistance?.text = "${car.distanceToEmpty} km"

        // 잠금 상태에 따른 색상 변경
        btnLock?.setColorFilter(
            if (car.isLocked) {
                ContextCompat.getColor(requireContext(), R.color.HyundaiBlue)
            } else {
                ContextCompat.getColor(requireContext(), R.color.black)
            }
        )

        // 잠금 해제 버튼 색상 변경
        btnUnlock?.setColorFilter(
            if (!car.isLocked) {
                ContextCompat.getColor(requireContext(), R.color.HyundaiBlue)
            } else {
                ContextCompat.getColor(requireContext(), R.color.black)
            }
        )

        // 시동 상태에 따른 색상 변경
        btnStart?.setColorFilter(
            if (car.isStarted) {
                ContextCompat.getColor(requireContext(), R.color.HyundaiBlue)
            } else {
                ContextCompat.getColor(requireContext(), R.color.black)
            }
        )
    }

    private fun setupRealtimeUpdates() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(currentUser)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), "실시간 업데이트 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    if (user != null && user.cars.isNotEmpty()) {
                        updateCarInfo(user.cars[0])
                    }
                }
            }
    }
}
