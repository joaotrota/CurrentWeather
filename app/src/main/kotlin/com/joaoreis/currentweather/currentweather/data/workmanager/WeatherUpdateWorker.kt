package com.joaoreis.currentweather.currentweather.data.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.joaoreis.currentweather.DataResult
import com.joaoreis.currentweather.currentweather.LocationProvider
import com.joaoreis.currentweather.currentweather.WeatherGateway
import com.joaoreis.currentweather.currentweather.WeatherPersistence

class WeatherUpdateWorker(
    context: Context,
    params: WorkerParameters,
    private val weatherGateway: WeatherGateway,
    private val weatherPersistence: WeatherPersistence,
    private val weatherUpdateConverter: WeatherUpdateConverter,
    private val locationProvider: LocationProvider
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return when (val location = locationProvider.getLocationData()) {
            is DataResult.Success -> {
                when (val remoteData = weatherGateway.getCurrentWeather(location.data.latitude, location.data.longitude)) {
                    is DataResult.Success -> {
                        weatherPersistence.saveWeather(remoteData.data)
                        setProgress(weatherUpdateConverter.convertToData(remoteData.data))
                        Result.success()
                    }
                    is DataResult.Error -> Result.failure()
                }

            }
            is DataResult.Error -> Result.failure()
        }
    }
}