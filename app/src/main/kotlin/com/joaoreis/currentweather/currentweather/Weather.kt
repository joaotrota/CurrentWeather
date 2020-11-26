package com.joaoreis.currentweather.currentweather

data class Weather(
    val description: String,
    val temperature: Double,
    val feelsLike: Double,
    val minimumTemperature: Double,
    val maximumTemperature: Double,
    val pressure: Int,
    val humidity: Int
)