package com.joaoreis.currentweather.currentweather

import com.joaoreis.currentweather.DataResult

interface WeatherGateway {
    fun getCurrentWeather(latitude: Double, longitude: Double) : DataResult<Weather>
}