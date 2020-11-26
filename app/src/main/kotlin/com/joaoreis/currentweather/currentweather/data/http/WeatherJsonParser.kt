package com.joaoreis.currentweather.currentweather.data.http

import com.joaoreis.currentweather.currentweather.Weather

interface WeatherJsonParser {
    fun parseWeather(data: String) : Weather
}