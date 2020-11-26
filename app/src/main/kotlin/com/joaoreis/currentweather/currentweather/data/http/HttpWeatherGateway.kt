package com.joaoreis.currentweather.currentweather.data.http

import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception
import com.joaoreis.currentweather.DataResult
import com.joaoreis.currentweather.currentweather.Weather
import com.joaoreis.currentweather.currentweather.WeatherGateway

class HttpWeatherGateway(
    private val baseUrl: String,
    private val apiKey: String,
    private val httpClient: OkHttpClient,
    private val weatherParser: WeatherJsonParser
) : WeatherGateway {
    override fun getCurrentWeather(latitude: Double, longitude: Double): DataResult<Weather> {
        return try {
            val request = Request.Builder()
                .url("${baseUrl}weather?lat=$latitude&lon=$longitude&appid=$apiKey&units=metric")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                DataResult.Success(weatherParser.parseWeather(response.body()!!.string()))
            } else {
                DataResult.Error()
            }
        } catch (e: Exception) {
            DataResult.Error()
        }
    }
}