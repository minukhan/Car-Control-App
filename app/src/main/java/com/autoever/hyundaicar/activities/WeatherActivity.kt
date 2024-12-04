package com.autoever.hyundaicar.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

class WeatherActivity : AppCompatActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weather)

        // 데이터 요청
        weatherViewModel.fetchWeatherData()

        // 데이터 관찰 및 UI 업데이트
        lifecycleScope.launch {
            weatherViewModel.weatherData.collect { weatherItems ->
                // 날씨 데이터를 화면에 표시
                weatherItems.forEach {
                    //println("Category: ${it.category}, Value: ${it.fcstValue}")
                }
            }
        }
    }
}