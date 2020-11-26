package com.joaoreis.currentweather

object TestData {

    const val STORED_WEATHER_JSON =
        """{"temp":282.55,"temp_min":280.37,"description":"clear sky","humidity":100,"pressure":1023,"feels_like":281.86,"temp_max":284.26}"""

    const val WEATHER_JSON: String = """
            {
              "coord": {
                "lon": -122.08,
                "lat": 37.39
              },
              "weather": [
                {
                  "id": 800,
                  "main": "Cloudy",
                  "description": "cloudy sky",
                  "icon": "01d"
                }
              ],
              "base": "stations",
              "main": {
                "temp": 182.55,
                "feels_like": 181.86,
                "temp_min": 180.37,
                "temp_max": 184.26,
                "pressure": 1923,
                "humidity": 99
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
        """
}