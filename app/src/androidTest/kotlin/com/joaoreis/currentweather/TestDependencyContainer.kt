package com.joaoreis.currentweather

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
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
import com.joaoreis.currentweather.di.DependencyContainer

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient

class TestDependencyContainer(
    private val sharedPreferences: SharedPreferences,
    private val okHttpClient: OkHttpClient,
    private val baseUrl: String,
    private val apiKey: String,
    private val locationData: LocationData? = null,
    private val applicationContext: Context,
    private val weatherUpdatesWorkTag: String = "WorkTag",
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
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
        FakeLocationProvider(locationData)
    }

    private val config = Configuration.Builder()
        .setMinimumLoggingLevel(Log.DEBUG)
        .setWorkerFactory(
            WeatherUpdateWorkerFactory(
                weatherGateway,
                weatherPersistence,
                weatherUpdateConverter,
                locationProvider
            )
        )
        .setExecutor(SynchronousExecutor())
        .build()


    override val periodicWeatherUpdater: PeriodicWeatherUpdater by lazy {
        WorkManagerTestInitHelper.initializeTestWorkManager(applicationContext, config)
        val workManager = WorkManager.getInstance(applicationContext)
        WorkManagerPeriodicWeatherUpdater(
            workManager,
            weatherUpdatesWorkTag,
            dispatcher,
            weatherUpdateConverter
        )
    }

    override val weatherInteractor: CurrentWeatherInteractor by lazy {
        CurrentWeatherInteractorImpl(
            dispatcher,
            weatherGateway,
            weatherPersistence,
            periodicWeatherUpdater,
            locationProvider
        )
    }
}