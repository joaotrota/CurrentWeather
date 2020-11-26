package com.joaoreis.currentweather.currentweather

import com.joaoreis.currentweather.DataResult

interface WeatherPersistence {
    fun getCurrentWeather() : DataResult<Weather>
    fun saveWeather(weather: Weather)
}