package com.joaoreis.currentweather

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.joaoreis.currentweather.currentweather.Weather
import com.joaoreis.currentweather.currentweather.data.local.StoredWeatherJsonObjectConverter
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class StoredJsonObjectConverterTest {

    @Test
    fun `Given a weather object When converted to json Then should have all fields correctly converted`() {
        val json = """{"temp":282.55,"temp_min":280.37,"description":"clear sky","humidity":100,"pressure":1023,"feels_like":281.86,"temp_max":284.26}"""

        val weather = Weather(
            description = "clear sky",
            temperature = 282.55,
            feelsLike = 281.86,
            minimumTemperature = 280.37,
            maximumTemperature = 284.26,
            pressure = 1023,
            humidity = 100
        )

        val parser = StoredWeatherJsonObjectConverter()

        assertEquals(json, parser.convertToString(weather))
    }

    @Test
    fun `Given a weather local json When converted to weather object Then should have all fields correctly converted`() {
        val json = """
            {
                "description": "clear sky",
                "temp": 282.55,
                "feels_like": 281.86,
                "temp_min": 280.37,
                "temp_max": 284.26,
                "pressure": 1023,
                "humidity": 100
            }
        """.trimIndent()

        val weather = Weather(
            description = "clear sky",
            temperature = 282.55,
            feelsLike = 281.86,
            minimumTemperature = 280.37,
            maximumTemperature = 284.26,
            pressure = 1023,
            humidity = 100
        )

        val parser = StoredWeatherJsonObjectConverter()

        assertEquals(weather, parser.convertToWeather(json))
    }
}