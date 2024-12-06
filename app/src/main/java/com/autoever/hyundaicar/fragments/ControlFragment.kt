package com.autoever.hyundaicar.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.models.Car
import com.autoever.hyundaicar.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ControlFragment : Fragment() {
    private var currentCar: Car? = null
    private lateinit var ivCarStatus: ImageView
    private lateinit var btnLock: ImageButton
    private lateinit var btnUnlock: ImageButton
    private lateinit var btnStart: ImageButton
    private lateinit var btnStop: ImageButton
    private lateinit var btnEmergency: ImageButton
    private lateinit var btnEmergencyStop: ImageButton
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_control, container, false)

        // 뷰 바인딩
        initializeViews(view)
        setupClickListeners()
        fetchCarData()

        return view
    }

    private fun initializeViews(view: View) {
        ivCarStatus = view.findViewById(R.id.ivCarStatus)
        btnLock = view.findViewById(R.id.btnLock)
        btnUnlock = view.findViewById(R.id.btnUnlock)
        btnStart = view.findViewById(R.id.btnStart)
        btnStop = view.findViewById(R.id.btnStop)
        btnEmergency = view.findViewById(R.id.btnEmergency)
        btnEmergencyStop = view.findViewById(R.id.btnEmergencyStop)
    }

    private fun setupClickListeners() {
        btnLock.setOnClickListener {
            currentCar?.let { car ->
                updateCarLockStatus(car.id, true)
                showToast("차량이 잠겼습니다.")
            }
        }

        btnUnlock.setOnClickListener {
            currentCar?.let { car ->
                updateCarLockStatus(car.id, false)
                showToast("차량이 잠금해제되었습니다.")
            }
        }

        btnStart.setOnClickListener {
            currentCar?.let { car ->
                if (!car.isStarted) {
                    updateCarEngineStatus(car.id, true)
                } else {
                    showToast("시동이 이미 켜져 있습니다.")
                }
            }
        }

        btnStop.setOnClickListener {
            currentCar?.let { car ->
                if (car.isStarted) {
                    updateCarEngineStatus(car.id, false)
                } else {
                    showToast("시동이 이미 꺼져 있습니다.")
                }
            }
        }

        btnEmergency.setOnClickListener {
            currentCar?.let { car ->
                // 현재 상태의 반대로 토글
                updateCarEmergencyStatus(car.id, !car.emergencyLight)
                val message = if (!car.emergencyLight) "비상등이 켜졌습니다." else "비상등이 꺼졌습니다."
                showToast(message)
            }
        }

        // 경적 버튼 - 누르고 있는 동안만 소리가 나도록 수정
        btnEmergencyStop.setOnClickListener {
            playSound(R.raw.car_honk)  // 경적 소리 재생
        }
    }

    private fun fetchCarData() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser?.uid ?: return

        val listener = db.collection("users")
            .document(currentUser)
            .addSnapshotListener { snapshot, e ->
                if (!isAdded) return@addSnapshotListener  // Fragment가 분리된 경우 리턴

                if (e != null) {
                    if (isAdded) {  // Toast 표시 전에도 확인
                        showToast("데이터 로드 오류: ${e.message}")
                    }
                    return@addSnapshotListener
                }

                snapshot?.toObject(User::class.java)?.let { user ->
                    if (user.cars.isNotEmpty()) {
                        currentCar = user.cars[0]
                        updateUI()
                    }
                }
            }

        // Fragment가 파괴될 때 리스너 제거를 위해 저장
        view?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {}
            override fun onViewDetachedFromWindow(v: View) {
                listener.remove()
            }
        })
    }

    private fun updateUI() {
        // Fragment가 context에 붙어있는지 확인
        if (!isAdded) return

        currentCar?.let { car ->
            try {
                // 버튼 색상 업데이트
                val colorBlue = ContextCompat.getColor(requireContext(), R.color.HyundaiBlue)
                val colorBlack = ContextCompat.getColor(requireContext(), R.color.black)

                btnLock.setColorFilter(if (car.isLocked) colorBlue else colorBlack)
                btnUnlock.setColorFilter(if (!car.isLocked) colorBlue else colorBlack)
                btnStart.setColorFilter(if (car.isStarted) colorBlue else colorBlack)
                btnStop.setColorFilter(if (!car.isStarted) colorBlue else colorBlack)
                btnEmergency.setColorFilter(if (car.emergencyLight) colorBlue else colorBlack)
            } catch (e: IllegalStateException) {
                // Fragment가 분리된 경우 예외 처리
            }
        }
    }

    private fun updateCarLockStatus(carId: String, isLocked: Boolean) {
        updateCarStatus(carId, mapOf("isLocked" to isLocked))
    }

    private fun updateCarEngineStatus(carId: String, isStarted: Boolean) {
        val message = if (isStarted) "시동이 켜졌습니다." else "시동이 꺼졌습니다."
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        updateCarStatus(carId, mapOf("isStarted" to isStarted))
        // 시동 소리 재생
        if (isStarted) {
            playSound(R.raw.engine_start)
            showToast("경적이 울립니다.")
        }
    }

    private fun updateCarEmergencyStatus(carId: String, isEmergency: Boolean) {
        updateCarStatus(carId, mapOf("emergencyLight" to isEmergency))
    }

    private fun updateCarStatus(carId: String, updates: Map<String, Any>) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(currentUser)
            .get()
            .addOnSuccessListener { document ->
                document.toObject(User::class.java)?.let { user ->
                    val updatedCars = user.cars.map { car ->
                        if (car.id == carId) {
                            var updatedCar = car
                            updates.forEach { (key, value) ->
                                when (key) {
                                    "isLocked" -> updatedCar = updatedCar.copy(isLocked = value as Boolean)
                                    "isStarted" -> updatedCar = updatedCar.copy(isStarted = value as Boolean)
                                    "emergencyLight" -> updatedCar = updatedCar.copy(emergencyLight = value as Boolean)
                                }
                            }
                            updatedCar
                        } else car
                    }

                    db.collection("users")
                        .document(currentUser)
                        .update("cars", updatedCars)
                        .addOnFailureListener { e ->
                            showToast("업데이트 실패: ${e.message}")
                        }
                }
            }
    }

    // 소리 재생을 위한 함수
    private fun playSound(resourceId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(requireContext(), resourceId).apply {
            setOnCompletionListener { release() }
            start()
        }
    }

    // MediaPlayer 정리
    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}