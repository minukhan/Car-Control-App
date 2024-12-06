package com.autoever.hyundaicar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.autoever.hyundaicar.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.InfoWindow

class MapFragment : Fragment(), com.naver.maps.map.OnMapReadyCallback {

    private var mapView: MapView? = null
    private var naverMap: NaverMap? = null
    private val markers = mutableListOf<Marker>() // 마커 목록

    data class LocationData(val location: LatLng, val name: String)

    private val gasStations = listOf(
        LocationData(LatLng(37.482632, 126.875964), "S-OIL 구광주유소"),
        LocationData(LatLng(37.484232, 126.888195), "영진주유소"),
        LocationData(LatLng(37.489102, 126.887723), "세화 석유"),
        LocationData(LatLng(37.491315, 126.884848), "구로 주유소"),
    )

    private val parkingLots = listOf(
        LocationData(LatLng(37.485591, 126.883074), "명일 주차장"),
        LocationData(LatLng(37.486136, 126.883868), "구일 주차장"),
        LocationData(LatLng(37.485744, 126.886142), "남구로주차장"),
        LocationData(LatLng(37.486340, 126.886400), "예술인주차장"),
    )

    private val CarWash = listOf(
        LocationData(LatLng(37.478592, 126.877422), "이화 세차장"),//여기까지함
        LocationData(LatLng(37.478033, 126.881085), "세홍 세차장"),
        LocationData(LatLng(37.477207, 126.879528), "쎌차디테일링"),
        LocationData(LatLng(37.480613, 126.888648), "미래 세차장"),
    )

    private val Cafe = listOf(
        LocationData(LatLng(37.480317, 126.878328), "카페 핸드메이드"),
        LocationData(LatLng(37.479400, 126.878183), "인크커피"),
        LocationData(LatLng(37.481577, 126.881900), "할리스 커피"),
        LocationData(LatLng(37.480924, 126.880866), "스타벅스"),
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        setupClickListeners(view)

        return view
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        // 지도 초기화
        naverMap.mapType = NaverMap.MapType.Basic
        naverMap.uiSettings.apply {
            isCompassEnabled = true
            isScaleBarEnabled = true
            isZoomControlEnabled = true
            isLocationButtonEnabled = true
        }

        // 초기 카메라 위치 설정
        val initialPosition = CameraPosition(LatLng(37.482249, 126.879607), 13.0)
        naverMap.cameraPosition = initialPosition

        // 마커 추가
        addMarker(LatLng(37.482249, 126.879607), "G90", "LG 가산디지털단지")
    }

    private var currentlyOpenInfoWindow: InfoWindow? = null // 현재 열린 InfoWindow를 추적

    private fun addMarker(position: LatLng, title: String, snippet: String) {
        val marker = Marker().apply {
            this.position = position
            this.map = naverMap
            this.captionText = title
            this.iconTintColor = 0xFFFF0000.toInt() // 빨강색
        }

        val infoWindow = InfoWindow().apply {
            adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return snippet
                }
            }
        }

        marker.setOnClickListener {
            // 현재 열린 InfoWindow가 있으면 닫기
            currentlyOpenInfoWindow?.close()

            // 현재 마커의 InfoWindow 열기
            if (currentlyOpenInfoWindow == infoWindow) {
                // 이미 열려 있는 경우 닫기만 수행
                currentlyOpenInfoWindow = null
            } else {
                infoWindow.open(marker)
                currentlyOpenInfoWindow = infoWindow
            }
            true
        }

        // "내 차량 위치" 마커는 항상 열림 상태 유지
        if (title == "내 차량 위치") {
            infoWindow.open(marker)
            currentlyOpenInfoWindow = infoWindow
        }
    }

    private fun setupClickListeners(view: View) {
        view.findViewById<ImageView>(R.id.imageView3)?.setOnClickListener {
            showCategoryMarkers(gasStations, "Gas Station")
        }
        view.findViewById<ImageView>(R.id.imageView4)?.setOnClickListener {
            showCategoryMarkers(parkingLots, "Parking Lot")
        }
        view.findViewById<ImageView>(R.id.imageView5)?.setOnClickListener {
            showCategoryMarkers(CarWash, "Wash Car")
        }
        view.findViewById<ImageView>(R.id.imageView7)?.setOnClickListener {
            showCategoryMarkers(Cafe, "Cafe")
        }
    }

    private fun showCategoryMarkers(locations: List<LocationData>, category: String) {
        // 기존 마커 제거
        markers.forEach { it.map = null }
        markers.clear()

        // 새로운 마커 추가
        locations.forEachIndexed { index, locationData ->
            val marker = Marker().apply {
                position = locationData.location
                map = naverMap
                captionText = "${locationData.name} (#${index + 1})"

                val infoWindow = InfoWindow()
                infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
                    override fun getText(infoWindow: InfoWindow): CharSequence {
                        return "${locationData.name}\n위도: ${locationData.location.latitude}, 경도: ${locationData.location.longitude}"
                    }
                }
                setOnClickListener {
                    infoWindow.open(this)
                    true
                }
            }
            markers.add(marker)
        }

        if (locations.isNotEmpty()) {
            val cameraUpdate = CameraUpdate.toCameraPosition(
                CameraPosition(locations[0].location, 15.0) // 위치와 줌 레벨 설정
            ).animate(CameraAnimation.Easing) // 부드러운 애니메이션 추가
            naverMap?.moveCamera(cameraUpdate)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
