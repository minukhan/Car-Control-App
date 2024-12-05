package com.autoever.hyundaicar.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
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
        setContentView(R.layout.activity_weather)

        val weatherDataText: TextView = findViewById(R.id.weatherDataText)

        weatherViewModel.fetchWeatherData()
        Log.d("WeatherViewModel", "jongsik Response: ${weatherViewModel.fetchWeatherData()}")
        lifecycleScope.launch {
            weatherViewModel.weatherData.collect { weatherItems ->
                val weatherInfo = weatherItems.joinToString("\n") { item ->
                    "Category: ${item.category}, Value: ${item.fcstValue}, 날짜: ${item.fcstDate}"
                }
                weatherDataText.text = weatherInfo
            }
        }
    }
}