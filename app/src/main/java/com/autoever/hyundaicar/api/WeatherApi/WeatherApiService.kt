package com.autoever.hyundaicar.api.WeatherApi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherApiService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(WeatherApi::class.java)

    suspend fun getWeatherData(): Weather {
        val serviceKey = "E%2BOqyHwg2ew5dYUpYUUkMCaUMyXsI37UWpgPg3q8N11kiy%2FzhodSqVsctbPS6oqXvdlOsXCKpr9UDcAy0YEjBg%3D%3D"
        val baseDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val baseTime = "0500"

        return api.getWeather(
            serviceKey = serviceKey,
            numOfRows = 10,
            pageNo = 1,
            baseDate = baseDate,
            baseTime = baseTime,
            nx = 55,
            ny = 127,
            dataType = "JSON"
        )
    }
}