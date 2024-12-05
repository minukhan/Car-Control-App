package com.autoever.hyundaicar.activities

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.models.Car
import com.autoever.hyundaicar.viewmodel.WeatherViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kakao.vectormap.KakaoMapSdk
import java.security.MessageDigest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /*try {
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.signingInfo?.apkContentsSigners
            } else {
                info.signatures
            }

            if (signatures != null) {
                for (signature in signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val keyHash = String(Base64.encode(md.digest(), Base64.DEFAULT))
                    Log.d("KeyHash", keyHash)
                    Toast.makeText(this, "KeyHash: $keyHash", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e("KeyHash", "Unable to get KeyHash", e)
        }*/

        // 뷰 참조
        val tvDate: TextView = findViewById(R.id.tvDate)
        val tvTemperature: TextView = findViewById(R.id.tvTemperature)

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

                val skyImageView = findViewById<ImageView>(R.id.SkyView)

                // 하늘 상태에 따라 이미지 변경
                val skyImageResource = when (skyCode) {
                    "1" -> R.drawable.sunny_icon  // 맑음
                    "3" -> R.drawable.cloudy_icon // 구름 많음
                    "4" -> R.drawable.cloud_icon // 흐림 (구름 많음과 동일 이미지로 설정 가능)
                    else -> R.drawable.sunny_icon // 기본값
                }

                // 이미지 뷰에 리소스 설정
                skyImageView.setImageResource(skyImageResource)


                // 날짜 포맷팅 및 요일 추가
                val formattedDate = if (rawDate != null && rawDate.length == 8) {
                    val year = rawDate.substring(0, 4) // 연도
                    val month = rawDate.substring(4, 6) // 월
                    val day = rawDate.substring(6, 8) // 일

                    // 날짜 문자열을 Date 객체로 변환
                    val dateString = "$year-$month-$day" // YYYY-MM-DD 형식
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date = dateFormat.parse(dateString)

                    // 요일 추출
                    val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // 요일
                    val dayOfWeek = dayOfWeekFormat.format(date)

                    "${year}년 ${month}월 ${day}일 $dayOfWeek" // 날짜 + 요일
                } else {
                    "Loading..."
                }

                // 뷰에 데이터 업데이트
                tvTemperature.text = temperatureData?.fcstValue?.let { "$it°C" } ?: "Loading..."
                tvDate.text = formattedDate
            }

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
    }


    // Firestore에 사용자 데이터 저장
    private fun saveCarData(car: Car) {
        val firestore = FirebaseFirestore.getInstance()

        // 사용자 UID를 Firestore 문서 ID로 사용하여 저장
        firestore.collection("cars")
            .add(car)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "차량 등록 완료", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "차량 등록 실패", Toast.LENGTH_SHORT).show()
            }
    }
}
