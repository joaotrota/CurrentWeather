package com.joaoreis.currentweather

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.joaoreis.currentweather.currentweather.Weather
import com.joaoreis.currentweather.currentweather.data.http.WeatherJsonObjectParser
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class WeatherJsonObjectParserTest {
    @Test
    fun `Given a json response When parse weather is called Then should return a weather object with the data`() {
        val weatherJson = """
            {
              "coord": {
                "lon": -122.08,
                "lat": 37.39
              },
              "weather": [
                {
                  "id": 800,
                  "main": "Clear",
                  "description": "clear sky",
                  "icon": "01d"
                }
              ],
              "base": "stations",
              "main": {
                "temp": 282.55,
                "feels_like": 281.86,
                "temp_min": 280.37,
                "temp_max": 284.26,
                "pressure": 1023,
                "humidity": 100
              },
              "visibility": 16093,
              "wind": {
                "speed": 1.5,
                "deg": 350
              },
              "clouds": {
                "all": 1
              },
              "dt": 1560350645,
              "sys": {
                "type": 1,
                "id": 5122,
                "message": 0.0139,
                "country": "US",
                "sunrise": 1560343627,
                "sunset": 1560396563
              },
              "timezone": -25200,
              "id": 420006353,
              "name": "Mountain View",
              "cod": 200
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

        val parser = WeatherJsonObjectParser()

        assertEquals(weather, parser.parseWeather(weatherJson))
    }
}