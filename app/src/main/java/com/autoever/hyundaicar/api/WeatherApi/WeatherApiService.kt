package com.autoever.hyundaicar.api.WeatherApi

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherApiService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .client(provideOkHttpClient()) // 로그 추가
        .build()

    private val api = retrofit.create(WeatherApi::class.java)

    suspend fun getWeatherData(): WeatherResponse {
        val serviceKey = "E+OqyHwg2ew5dYUpYUUkMCaUMyXsI37UWpgPg3q8N11kiy/zhodSqVsctbPS6oqXvdlOsXCKpr9UDcAy0YEjBg=="
        val baseDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val baseTime = "1400"

        try {
            val response = api.getWeather(
                serviceKey = serviceKey,
                numOfRows = 10,
                pageNo = 1,
                baseDate = baseDate,
                baseTime = baseTime,
                nx = 58,
                ny = 125,
                dataType = "JSON"
            )
            Log.d("WeatherApiService", "Raw Response: ${response.response}")
            return response
        } catch (e: Exception) {
            Log.e("WeatherApiService", "API 호출 실패", e)
            throw e
        }
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
}
