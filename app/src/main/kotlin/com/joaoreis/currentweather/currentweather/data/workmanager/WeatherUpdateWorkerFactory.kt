package com.joaoreis.currentweather.currentweather.data.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.joaoreis.currentweather.currentweather.LocationProvider
import com.joaoreis.currentweather.currentweather.WeatherGateway
import com.joaoreis.currentweather.currentweather.WeatherPersistence

class WeatherUpdateWorkerFactory(
    private val weatherGateway: WeatherGateway,
    private val weatherPersistence: WeatherPersistence,
    private val weatherUpdateConverter: WeatherUpdateConverter,
    private val locationProvider: LocationProvider
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when(workerClassName) {
            WeatherUpdateWorker::class.java.name ->
                WeatherUpdateWorker(
                    appContext,
                    workerParameters,
                    weatherGateway,
                    weatherPersistence,
                    weatherUpdateConverter,
                    locationProvider
                )
            else -> null
        }
    }
}