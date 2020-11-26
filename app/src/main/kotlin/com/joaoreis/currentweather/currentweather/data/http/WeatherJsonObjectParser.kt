package com.joaoreis.currentweather.currentweather.data.http

import com.joaoreis.currentweather.currentweather.Weather
import org.json.JSONObject

class WeatherJsonObjectParser : WeatherJsonParser {
    override fun parseWeather(data: String): Weather {
        val weatherJson = JSONObject(data)

        val weather = weatherJson.getJSONArray("weather").getJSONObject(0)
        val main = weatherJson.getJSONObject("main")

        return Weather(
            description = weather.getString("description"),
            temperature = main.getDouble("temp"),
            feelsLike = main.getDouble("feels_like"),
            minimumTemperature = main.getDouble("temp_min"),
            maximumTemperature = main.getDouble("temp_max"),
            pressure = main.getInt("pressure"),
            humidity = main.getInt("humidity")
        )
    }
}