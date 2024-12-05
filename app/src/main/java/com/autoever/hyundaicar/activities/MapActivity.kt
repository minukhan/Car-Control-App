package com.autoever.hyundaicar.activities

import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.autoever.hyundaicar.R
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView


class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    var kakaoMap: KakaoMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        KakaoMapSdk.init(this, "7511dbb0f9b175684ca5ae53be2998d4");

        mapView = findViewById<MapView>(R.id.map_view)
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출
                Log.d("KakaoMap", "onMapDestroy: ")
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출
                Log.e("KakaoMap", "onMapError: ", error)
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(@NonNull map: KakaoMap) {
                // 정상적으로 인증이 완료되었을 때 호출
                // KakaoMap 객체를 얻어 옵니다.
                kakaoMap = map
            }
        })
    }
}
