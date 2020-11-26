package com.joaoreis.currentweather

import android.app.Application
import android.content.Context
import com.joaoreis.currentweather.di.*

import okhttp3.OkHttpClient

class WeatherApplication : Application(),
    DependencyProvider {

    private val baseUrl = "https://api.openweathermap.org/data/2.5/"

    private val apiKey = "5ad7218f2e11df834b0eaf3a33a39d2a"

    private val okHttpClient = OkHttpClient.Builder().build()

    private val sharedPreferences by lazy {
        getSharedPreferences(
            "WEATHER_PREF",
            Context.MODE_PRIVATE
        )
    }

    private val dependencyContainer: DependencyContainer by lazy {
        DependencyContainerImpl(
            sharedPreferences,
            okHttpClient,
            baseUrl,
            apiKey,
            this,
            "WeatherUpdates"
        )
    }

    override lateinit var currentWeatherComponent: CurrentWeatherComponent

    override fun onCreate() {
        super.onCreate()
        currentWeatherComponent = CurrentWeatherComponentImpl(dependencyContainer.weatherInteractor)
    }
}