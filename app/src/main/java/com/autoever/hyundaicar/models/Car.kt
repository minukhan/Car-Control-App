package com.autoever.hyundaicar.models

data class Car(
    var id: String = "",
    var name: String = "",
    var image: String = "",
    var isStarted: Boolean = false,
    var isLocked: Boolean = true,
    var temperature: Double = 0.0,
    var distanceToEmpty: Int = 0,
    var location: Location = Location()
)

data class Location(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)
