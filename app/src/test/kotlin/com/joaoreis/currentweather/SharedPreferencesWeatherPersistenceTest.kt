package com.joaoreis.currentweather

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.joaoreis.currentweather.currentweather.Weather
import com.joaoreis.currentweather.currentweather.data.local.SharedPreferencesWeatherPersistence
import com.joaoreis.currentweather.currentweather.data.local.StoredWeatherConverter
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class SharedPreferencesWeatherPersistenceTest {

    @Test
    fun `Given a weather object When save is called Then should save the weather object`() {

        val sharedPreferences = ApplicationProvider.getApplicationContext<TestApplication>()
            .getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)

        val weather = Weather(
            "Clear",
            22.toDouble(),
            23.toDouble(),
            10.toDouble(),
            25.toDouble(),
            1000,
            99
        )

        val storedWeatherConverter = mockk<StoredWeatherConverter>()
        every {
            storedWeatherConverter.convertToString(any())
        } returns "weather"

        every {
            storedWeatherConverter.convertToWeather("weather")
        } returns weather

        val persistence = SharedPreferencesWeatherPersistence(
            sharedPreferences,
            storedWeatherConverter
        )

        persistence.saveWeather(weather)

        assertEquals(DataResult.Success(weather), persistence.getCurrentWeather())
    }

    @Test
    fun `Given there is no saved weather When current weather is loaded Then should return error`() {
        val sharedPreferences = ApplicationProvider.getApplicationContext<TestApplication>()
            .getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)

        val weather = Weather(
            "Clear",
            22.toDouble(),
            23.toDouble(),
            10.toDouble(),
            25.toDouble(),
            1000,
            99
        )

        val storedWeatherConverter = mockk<StoredWeatherConverter>(relaxed = true)

        val persistence = SharedPreferencesWeatherPersistence(
            sharedPreferences,
            storedWeatherConverter
        )

        persistence.saveWeather(weather)

        assert(persistence.getCurrentWeather() is DataResult.Error<Weather>)
    }

}