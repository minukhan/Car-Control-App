package com.autoever.hyundaicar.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.models.Car
import com.autoever.hyundaicar.models.Location
import com.autoever.hyundaicar.models.User
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch

class SelectCarActivity : AppCompatActivity() {
    private val cars: MutableList<Car> = mutableListOf(
        Car(
            id = "1",
            name = "아반떼 N",
            image = "https://www.hyundai.com/contents/repn-car/side-45/main-avante-n-25my-45side.png",
            isStarted = false,
            isLocked = true,
            temperature = 20.1,
            distanceToEmpty = 247,
            location = Location(37.5665, 126.978),
            emergencyLight = false
        ),
        Car(
            id = "2",
            name = "팰리세이드",
            image = "https://www.hyundai.com/contents/repn-car/side-45/main-palisade-24my-45side.png",
            isStarted = false,
            isLocked = true,
            temperature = 20.1,
            distanceToEmpty = 89,
            location = Location(37.5665, 126.978),
            emergencyLight = false
        ),
        Car(
            id = "3",
            name = "캐스퍼",
            image = "https://casper.hyundai.com/wcontents/repn-car/side-45/AX03/AXJJ4VPT3/A04/TKS/m.png",
            isStarted = false,
            isLocked = true,
            temperature = 20.1,
            distanceToEmpty = 173,
            location = Location(37.5665, 126.978),
            emergencyLight = false
        ),
        Car(
            id = "4",
            name = "그랜져",
            image = "https://www.hyundai.com/contents/repn-car/side-45/main-grandeur-taxi-25my-45side.png",
            isStarted = false,
            isLocked = true,
            temperature = 20.1,
            distanceToEmpty = 384,
            location = Location(37.5665, 126.978),
            emergencyLight = false
        ),
        Car(
            id = "5",
            name = "아반떼 하이브리드",
            image = "https://www.hyundai.com/contents/repn-car/side-45/main-avante-hybrid-25my-45side.png",
            isStarted = false,
            isLocked = true,
            temperature = 20.1,
            distanceToEmpty = 76,
            location = Location(37.5665, 126.978),
            emergencyLight = false
        ),
        Car(
            id = "6",
            name = "쏘나타",
            image = "https://www.hyundai.com/contents/repn-car/side-45/main-sonata-the-edge-hybrid-25my-45side.png",
            isStarted = false,
            isLocked = true,
            temperature = 20.1,
            distanceToEmpty = 512,
            location = Location(37.5665, 126.978),
            emergencyLight = false
        ),
        Car(
            id = "7",
            name = "코나",
            image = "https://www.hyundai.com/contents/repn-car/side-45/main-kona-24my-45side.png",
            isStarted = false,
            isLocked = true,
            temperature = 20.1,
            distanceToEmpty = 429,
            location = Location(37.5665, 126.978),
            emergencyLight = false
        ),
        Car(
            id = "8",
            name = "싼타페",
            image = "https://www.hyundai.com/contents/repn-car/side-45/main-santafe-hybrid-25my-45side.png",
            isStarted = false,
            isLocked = true,
            temperature = 20.1,
            distanceToEmpty = 324,
            location = Location(37.5665, 126.978),
            emergencyLight = false
        ),
        Car(
            id = "9",
            name = "스타리아",
            image = "https://www.hyundai.com/contents/repn-car/side-45/main-staria-lounge-limousine-24my-45side.png",
            isStarted = false,
            isLocked = true,
            temperature = 20.1,
            distanceToEmpty = 147,
            location = Location(37.5665, 126.978),
            emergencyLight = false
        ),
        Car(
            id = "10",
            name = "아이오닉 5",
            image = "https://www.hyundai.com/contents/repn-car/side-45/the-new-ioniq5-24pe-45side.png",
            isStarted = false,
            isLocked = true,
            temperature = 20.1,
            distanceToEmpty = 231,
            location = Location(37.5665, 126.978),
            emergencyLight = false
        ),

    )
    private lateinit var adapter: CarAdapter
    private var selectedCar: Car? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val btnConfirm = findViewById<TextView>(R.id.btnConfirm)

        // 그리드 레이아웃 매니저 설정
        val layoutManager = GridLayoutManager(this, 1)
        recyclerView.layoutManager = layoutManager

        // 어댑터 초기화 (클릭 리스너 추가)
        adapter = CarAdapter(cars) { car ->
            selectedCar = car
            btnConfirm.isEnabled = true
        }
        recyclerView.adapter = adapter

        // Firebase 초기화
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 선택 버튼 초기 상태 설정
        btnConfirm.isEnabled = false

        // 선택 완료 버튼 동작
        btnConfirm.setOnClickListener {
            selectedCar?.let { car ->
                // TODO: 선택된 차량을 사용자의 차량 목록에 추가하는 API 호출
                saveSelectedCar(car)
            }
        }
    }

    private fun saveSelectedCar(car: Car) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "사용자 인증 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = db.collection("users").document(currentUser.uid)

        userRef.get().addOnSuccessListener { document ->
            // 현재 사용자의 데이터 가져오기
            val user = document.toObject(User::class.java)

            // 새로운 차량 추가
            val updatedCars = (user?.cars ?: emptyList()) + car

            // Firestore 업데이트
            userRef.update("cars", updatedCars)
                .addOnSuccessListener {
                    // MainActivity로 이동
                    Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(this)
                    }
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this,
                        "차량 추가 중 오류가 발생했습니다: ${e.message}",
                        Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener { e ->
            Toast.makeText(this,
                "사용자 정보를 가져오는 중 오류가 발생했습니다: ${e.message}",
                Toast.LENGTH_SHORT).show()
        }
    }
}

class CarAdapter(
    private val cars: List<Car>,
    private var selectedPosition: Int = -1,
    private val onItemClick: (Car) -> Unit
) : RecyclerView.Adapter<CarAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewCarName: TextView = itemView.findViewById(R.id.textViewCarName)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val cardView: MaterialCardView = itemView.findViewById(R.id.cardView) // 아이템의 루트 뷰
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val car = cars[position]
        holder.textViewCarName.text = car.name

        // 선택 상태에 따른 UI 업데이트
        holder.cardView.isChecked = position == selectedPosition
        holder.cardView.strokeWidth = if (position == selectedPosition) 4 else 0
        holder.cardView.strokeColor = if (position == selectedPosition) {
            ContextCompat.getColor(holder.itemView.context, R.color.HyundaiNavy)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.white)
        }

        Glide.with(holder.itemView.context)
            .load(car.image)
            .placeholder(R.drawable.car_image)
            .error(R.drawable.car_image)
            .into(holder.imageView)

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onItemClick(car)
        }
    }

    override fun getItemCount() = cars.size
}