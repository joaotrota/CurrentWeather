package com.example.sandbox

import com.joaoreis.currentweather.DataResult
import com.joaoreis.currentweather.currentweather.Weather
import com.joaoreis.currentweather.currentweather.WeatherGateway

class FakeWeatherGateway(private val weather: Weather? = null) : WeatherGateway {
    override fun getCurrentWeather(latitude: Double, longitude: Double): DataResult<Weather> {
        return if (weather != null) {
            DataResult.Success(weather)
        } else {
            DataResult.Error()
        }
    }
}