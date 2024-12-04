package com.autoever.hyundaicar.models

data class User(
    var id: String = "",
    val email: String = "",
    val nickname: String = "",
    val cars: List<Car> = emptyList()
)
