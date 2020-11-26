package com.joaoreis.currentweather.currentweather.data.local

import com.joaoreis.currentweather.currentweather.Weather


interface StoredWeatherConverter {
    fun convertToString(weather: Weather) : String
    fun convertToWeather(data: String) : Weather
}