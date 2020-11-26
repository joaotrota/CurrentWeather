package com.joaoreis.currentweather.di

import android.content.Context
import android.content.SharedPreferences
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.android.gms.location.LocationServices
import com.joaoreis.currentweather.currentweather.*
import com.joaoreis.currentweather.currentweather.data.http.HttpWeatherGateway
import com.joaoreis.currentweather.currentweather.data.http.WeatherJsonObjectParser
import com.joaoreis.currentweather.currentweather.data.http.WeatherJsonParser
import com.joaoreis.currentweather.currentweather.data.local.SharedPreferencesWeatherPersistence
import com.joaoreis.currentweather.currentweather.data.local.StoredWeatherConverter
import com.joaoreis.currentweather.currentweather.data.local.StoredWeatherJsonObjectConverter
import com.joaoreis.currentweather.currentweather.data.workmanager.WeatherUpdateConverter
import com.joaoreis.currentweather.currentweather.data.workmanager.WeatherUpdateWorkerFactory
import com.joaoreis.currentweather.currentweather.data.workmanager.WorkManagerPeriodicWeatherUpdater
import com.joaoreis.currentweather.currentweather.data.workmanager.WorkManagerWeatherUpdateConverter
import com.joaoreis.currentweather.framework.AndroidLocationProvider

import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient

class DependencyContainerImpl(
    private val sharedPreferences: SharedPreferences,
    private val okHttpClient: OkHttpClient,
    private val baseUrl: String,
    private val apiKey: String,
    private val applicationContext: Context,
    private val weatherUpdatesWorkTag: String
) : DependencyContainer {

    override val weatherUpdateConverter: WeatherUpdateConverter by lazy {
        WorkManagerWeatherUpdateConverter()
    }

    override val weatherJsonParser: WeatherJsonParser by lazy {
        WeatherJsonObjectParser()
    }

    override val storedWeatherConverter: StoredWeatherConverter by lazy {
        StoredWeatherJsonObjectConverter()
    }


    override val weatherPersistence: WeatherPersistence by lazy {
        SharedPreferencesWeatherPersistence(sharedPreferences, storedWeatherConverter)
    }

    override val weatherGateway: WeatherGateway by lazy {
        HttpWeatherGateway(baseUrl, apiKey, okHttpClient, weatherJsonParser)
    }

    override val locationProvider: LocationProvider by lazy {
        AndroidLocationProvider(
            LocationServices.getFusedLocationProviderClient(applicationContext),
            Dispatchers.Default
        )
    }

    private val workManagerConfiguration = Configuration.Builder()
        .setMinimumLoggingLevel(android.util.Log.INFO)
        .setWorkerFactory(
            WeatherUpdateWorkerFactory(
                weatherGateway,
                weatherPersistence,
                weatherUpdateConverter,
                locationProvider
            )
        )
        .build()

    override val periodicWeatherUpdater: PeriodicWeatherUpdater by lazy {
        WorkManager.initialize(applicationContext, workManagerConfiguration)
        val workManager = WorkManager.getInstance(applicationContext)
        WorkManagerPeriodicWeatherUpdater(
            workManager,
            weatherUpdatesWorkTag,
            Dispatchers.Default,
            weatherUpdateConverter
        )
    }

    override val weatherInteractor: CurrentWeatherInteractor by lazy {
        CurrentWeatherInteractorImpl(
            Dispatchers.Default,
            weatherGateway,
            weatherPersistence,
            periodicWeatherUpdater,
            locationProvider
        )
    }
}