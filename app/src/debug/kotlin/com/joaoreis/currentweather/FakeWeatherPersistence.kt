package com.joaoreis.currentweather

import com.joaoreis.currentweather.currentweather.Weather
import com.joaoreis.currentweather.currentweather.WeatherPersistence

class FakeWeatherPersistence(private val weather: Weather? = null) : WeatherPersistence {
    override fun getCurrentWeather(): DataResult<Weather> {
        return if (weather != null) {
            DataResult.Success(weather)
        } else {
            DataResult.Error()
        }
    }

    override fun saveWeather(weather: Weather) {
    }

}