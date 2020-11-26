package com.joaoreis.currentweather.currentweather.data.workmanager

import androidx.work.Data
import com.joaoreis.currentweather.currentweather.Weather

interface WeatherUpdateConverter {
    fun convertToWeather(data: Data) : Weather
    fun convertToData(weather: Weather) : Data
}