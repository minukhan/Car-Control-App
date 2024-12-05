package com.autoever.hyundaicar.activities

import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.api.WeatherApi.WeatherApiService

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

    }
}
