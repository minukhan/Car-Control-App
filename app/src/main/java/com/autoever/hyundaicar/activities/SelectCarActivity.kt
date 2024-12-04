package com.autoever.hyundaicar.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autoever.hyundaicar.R
import com.autoever.hyundaicar.models.Car
import com.autoever.hyundaicar.models.Location
import com.bumptech.glide.Glide

class SelectCarActivity : AppCompatActivity() {
    // 더미 데이터
    val cars: MutableList<Car> = mutableListOf(
        Car (
            id = "1",
            name = "G70",
            image = "",
            isStarted = false,
            isLocked = true,
            temperature = 0.0,
            distanceToEmpty = 0,
            location = Location(0.0, 0.0)
        ),
        Car (
            id = "2",
            name = "G80",
            image = "",
            isStarted = false,
            isLocked = true,
            temperature = 0.0,
            distanceToEmpty = 0,
            location = Location(0.0, 0.0)
        ),
        Car (
            id = "3",
            name = "G90",
            image = "",
            isStarted = false,
            isLocked = true,
            temperature = 0.0,
            distanceToEmpty = 0,
            location = Location(0.0, 0.0)
        ),
        Car (
            id = "4",
            name = "GV70",
            image = "",
            isStarted = false,
            isLocked = true,
            temperature = 0.0,
            distanceToEmpty = 0,
            location = Location(0.0, 0.0)
        )

    )
    private lateinit var adapter: CarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car)

        // View 초기화
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val btnConfirm = findViewById<TextView>(R.id.btnConfirm)

        // 그리드 레이아웃 매니저 설정
        val layoutManager = GridLayoutManager(this, 1) // 열 개수 설정
        recyclerView.layoutManager = layoutManager

        // 데이터 설정
        adapter = CarAdapter(cars)
        recyclerView.adapter = adapter

        // 자동차 정보 불러오기
//        fetchCars()

        // 선택 버튼 동작
        btnConfirm.setOnClickListener {

        }
    }

//    fun fetchCars() {
//        TODO("RetrofitInstance 클래스 필요")
//        lifecycleScope.launch {
//            try {
//                val response = RetrofitInstance.api.getCars()
//                if (response.isSuccessful && response.body() != null) {
//                    withContext(Dispatchers.Main) {
//                        cars.clear()
//                        cars.addAll(response.body()!!)
//                        adapter.notifyDataSetChanged()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
}

class CarAdapter(private val cars: List<Car>) : RecyclerView.Adapter<CarAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewCarName: TextView = itemView.findViewById(R.id.textViewCarName)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car, parent, false) // 아이템 레이아웃 추가

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val car = cars[position]
        holder.textViewCarName.text = car.name

        Glide.with(holder.itemView.context)
            .load(car.image)
            .placeholder(R.drawable.car_image)
            .error(R.drawable.car_image)
            .into(holder.imageView)
    }

    override fun getItemCount() = cars.size
}