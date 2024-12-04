package com.autoever.hyundaicar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoever.hyundaicar.api.WeatherApi.Weather
import com.autoever.hyundaicar.api.WeatherApi.WeatherApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val weatherApiService = WeatherApiService()

    private val _weatherData = MutableStateFlow<List<Weather>>(emptyList())
    val weatherData: StateFlow<List<Weather>> = _weatherData

    fun fetchWeatherData() {
        viewModelScope.launch {
            try {
                val response = weatherApiService.getWeatherData()
                //_weatherData.value = response.response.body.items.item
            } catch (e: Exception) {
                e.printStackTrace() // 에러 처리
            }
        }
    }
}
