package com.autoever.hyundaicar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.autoever.hyundaicar.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.NaverMapOptions

class MapFragment : Fragment(), com.naver.maps.map.OnMapReadyCallback {

    private var mapView: MapView? = null
    private var naverMap: NaverMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // MapView 초기화
        mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)  // 생명주기 관리
        mapView?.getMapAsync(this)  // 네이버 지도 비동기로 초기화

        return view
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        // 기본 지도 설정
        naverMap.mapType = NaverMap.MapType.Basic // 기본 지도 타입
        naverMap.uiSettings.isZoomControlEnabled = true  // 줌 버튼 활성화

        // 지도 위치 설정 (서울로 설정 예시)
        /*val cameraPosition = CameraPosition(LatLng(37.5665, 126.9780), 10.0) // 서울 중심 좌표
        naverMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))*/

        // 마커 추가
        val marker = Marker()
        marker.position = LatLng(37.4820434, 126.8795371) // 서울 좌표
        marker.map = naverMap // 마커를 지도에 표시

        /*// 정보창 추가
        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(requireContext()) {
            override fun getContentView(infoWindow: InfoWindow): View {
                val view = layoutInflater.inflate(R.layout.info_window, null)
                // 정보창에 내용 설정
                return view
            }
        }
        infoWindow.open(marker)*/  // 마커 클릭 시 정보창 표시
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
}
