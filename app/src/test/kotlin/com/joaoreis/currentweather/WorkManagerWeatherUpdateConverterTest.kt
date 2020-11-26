package com.joaoreis.currentweather

import androidx.work.Data
import com.joaoreis.currentweather.currentweather.Weather
import com.joaoreis.currentweather.currentweather.data.workmanager.WorkManagerWeatherUpdateConverter
import junit.framework.Assert.assertEquals
import org.junit.Test

class WorkManagerWeatherUpdateConverterTest {

    @Test
    fun `Given a weather object When converted to data Then should create a Data object with the correct values`() {
        val weather = Weather(
            "Clear",
            22.toDouble(),
            23.toDouble(),
            10.toDouble(),
            25.toDouble(),
            1000,
            99
        )

        val expectedData = Data.Builder().putString("description", "Clear")
            .putDouble("temperature", 22.toDouble())
            .putDouble("feels_like", 23.toDouble())
            .putDouble("minimum_temperature", 10.toDouble())
            .putDouble("maximum_temperature", 25.toDouble())
            .putInt("pressure", 1000)
            .putInt("humidity", 99)
            .build()

        val converter = WorkManagerWeatherUpdateConverter()

        assertEquals(expectedData, converter.convertToData(weather))
    }


    @Test
    fun `Given a data object When converted to weather Then should create a weather object with the correct values`() {
        val expectedWeather = Weather(
            "Clear",
            22.toDouble(),
            23.toDouble(),
            10.toDouble(),
            25.toDouble(),
            1000,
            99
        )

        val data = Data.Builder().putString("description", "Clear")
            .putDouble("temperature", 22.toDouble())
            .putDouble("feels_like", 23.toDouble())
            .putDouble("minimum_temperature", 10.toDouble())
            .putDouble("maximum_temperature", 25.toDouble())
            .putInt("pressure", 1000)
            .putInt("humidity", 99)
            .build()

        val converter = WorkManagerWeatherUpdateConverter()

        assertEquals(expectedWeather, converter.convertToWeather(data))
    }
}