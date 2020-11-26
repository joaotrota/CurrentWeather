package com.joaoreis.currentweather


import com.joaoreis.currentweather.currentweather.Weather
import com.joaoreis.currentweather.currentweather.data.http.HttpWeatherGateway
import com.joaoreis.currentweather.currentweather.data.http.WeatherJsonParser
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.handleCoroutineException
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test

class HttpWeatherGatewayTest {

    @Test
    fun `Given the API is working When weather is requested Then should call the correct endpoint with GET And with correct parameters And return the parsed weather object`() {
        val server = MockWebServer()
        server.start()
        val baseUrl = server.url("/").toString()
        val latitude = 1.0
        val longitude = 2.0
        val apiKey = "123"

        val weather = Weather(
            "Clear",
            22.toDouble(),
            23.toDouble(),
            10.toDouble(),
            25.toDouble(),
            1000,
            99
        )

        val weatherParser = mockk<WeatherJsonParser>().also {
            every { it.parseWeather(any()) } returns weather
        }

        val httpWeatherGateway = HttpWeatherGateway(
            baseUrl = baseUrl,
            apiKey = apiKey,
            httpClient = OkHttpClient(),
            weatherParser = weatherParser
        )

        server.enqueue(MockResponse().setResponseCode(200))

        val result = httpWeatherGateway.getCurrentWeather(1.0, 2.0)

        val request = server.takeRequest()

        assertEquals("GET", request.method)
        assertEquals("/weather?lat=$latitude&lon=$longitude&appid=$apiKey&units=metric", request.path)
        assertEquals(result, DataResult.Success(weather))
        server.shutdown()
    }

    @Test
    fun `Given the API is not working When weather is requested Then should return error result`() {
        val server = MockWebServer()
        server.start()
        val baseUrl = server.url("/").toString()
        val latitude = 1.0
        val longitude = 2.0
        val apiKey = "123"

        val httpWeatherGateway = HttpWeatherGateway(
            baseUrl = baseUrl,
            apiKey = apiKey,
            httpClient = OkHttpClient(),
            weatherParser = mockk(relaxed = true)
        )

        server.enqueue(MockResponse().setResponseCode(400))

        val result = httpWeatherGateway.getCurrentWeather(1.0, 2.0)

        val request = server.takeRequest()

        assertEquals("GET", request.method)
        assertEquals("/weather?lat=$latitude&lon=$longitude&appid=$apiKey&units=metric", request.path)
        assert(result is DataResult.Error)

        server.shutdown()
    }

    @Test
    fun `Given the http client is not working When weather is requested Then should return error result`() {
        val httpClient = mockk<OkHttpClient>(relaxed = true)

        every { httpClient.newCall(any()).execute() } throws Exception()

        val apiKey = "123"

        val httpWeatherGateway = HttpWeatherGateway(
            baseUrl = "/",
            apiKey = apiKey,
            httpClient = OkHttpClient(),
            weatherParser = mockk(relaxed = true)
        )

        val result = httpWeatherGateway.getCurrentWeather(1.0, 2.0)

        assert(result is DataResult.Error)

    }
}