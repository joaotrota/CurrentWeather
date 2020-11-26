package com.joaoreis.currentweather.currentweather.data.local

import android.content.SharedPreferences
import com.joaoreis.currentweather.DataResult
import com.joaoreis.currentweather.currentweather.Weather
import com.joaoreis.currentweather.currentweather.WeatherPersistence

class SharedPreferencesWeatherPersistence(
    private val sharedPreferences: SharedPreferences,
    private val storedWeatherConverter: StoredWeatherConverter
) : WeatherPersistence {

    private val weatherKey = "WEATHER_KEY"

    override fun getCurrentWeather(): DataResult<Weather> {
        val storedWeather = sharedPreferences.getString(weatherKey, "")
        return if (storedWeather.isNullOrBlank()) {
            DataResult.Error()
        } else {
            DataResult.Success(storedWeatherConverter.convertToWeather(storedWeather))
        }
    }

    override fun saveWeather(weather: Weather) {
        sharedPreferences
            .edit()
            .putString(weatherKey, storedWeatherConverter.convertToString(weather))
            .apply()
    }
}