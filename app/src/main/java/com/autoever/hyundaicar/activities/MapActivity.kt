package com.autoever.hyundaicar.activities

import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.api.WeatherApi.WeatherApiService

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    var kakaoMap: KakaoMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
