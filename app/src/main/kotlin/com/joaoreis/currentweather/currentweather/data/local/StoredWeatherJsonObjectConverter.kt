package com.joaoreis.currentweather.currentweather.data.local

import com.joaoreis.currentweather.currentweather.Weather
import org.json.JSONObject

class StoredWeatherJsonObjectConverter : StoredWeatherConverter {
    override fun convertToString(weather: Weather): String {
        val jsonObject = JSONObject()
        jsonObject.put("description", weather.description)
        jsonObject.put("temp", weather.temperature)
        jsonObject.put("feels_like", weather.feelsLike)
        jsonObject.put("temp_min", weather.minimumTemperature)
        jsonObject.put("temp_max", weather.maximumTemperature)
        jsonObject.put("pressure", weather.pressure)
        jsonObject.put("humidity", weather.humidity)

        return jsonObject.toString()
    }

    override fun convertToWeather(data: String): Weather {
        val weatherJsonObject = JSONObject(data)

        return Weather(
            description = weatherJsonObject.getString("description"),
            temperature = weatherJsonObject.getDouble("temp"),
            feelsLike = weatherJsonObject.getDouble("feels_like"),
            minimumTemperature = weatherJsonObject.getDouble("temp_min"),
            maximumTemperature = weatherJsonObject.getDouble("temp_max"),
            pressure = weatherJsonObject.getInt("pressure"),
            humidity = weatherJsonObject.getInt("humidity")
        )
    }
}