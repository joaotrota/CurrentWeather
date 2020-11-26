package com.joaoreis.currentweather.currentweather.data.workmanager

import androidx.work.Data
import com.joaoreis.currentweather.currentweather.Weather

class WorkManagerWeatherUpdateConverter : WeatherUpdateConverter {
    override fun convertToWeather(data: Data): Weather {
        return Weather(
            description = data.getString("description")!!,
            temperature = data.getDouble("temperature", 0.0),
            feelsLike = data.getDouble("feels_like", 0.0),
            minimumTemperature = data.getDouble("minimum_temperature", 0.0),
            maximumTemperature = data.getDouble("maximum_temperature", 0.0),
            pressure = data.getInt("pressure", 0),
            humidity = data.getInt("humidity", 0),
        )
    }

    override fun convertToData(weather: Weather): Data {
        return Data.Builder().putString("description", weather.description)
            .putDouble("temperature", weather.temperature)
            .putDouble("feels_like", weather.feelsLike)
            .putDouble("minimum_temperature", weather.minimumTemperature)
            .putDouble("maximum_temperature", weather.maximumTemperature)
            .putInt("pressure", weather.pressure)
            .putInt("humidity", weather.humidity)
            .build()
    }
}