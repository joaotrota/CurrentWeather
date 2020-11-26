package com.joaoreis.currentweather.currentweather.data.workmanager

import androidx.lifecycle.asFlow
import androidx.work.*
import com.joaoreis.currentweather.currentweather.PeriodicWeatherUpdater
import com.joaoreis.currentweather.currentweather.Weather
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class WorkManagerPeriodicWeatherUpdater(
    private val workManager: WorkManager,
    private val workTag: String,
    private val dispatcher: CoroutineDispatcher,
    private val weatherUpdateConverter: WeatherUpdateConverter
) : PeriodicWeatherUpdater {

    private val _weatherUpdates = MutableSharedFlow<Weather>()

    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    override fun startUpdates() {
        periodicWorkRequest = PeriodicWorkRequestBuilder<WeatherUpdateWorker>(2, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
            )
            .setInitialDelay(2, TimeUnit.HOURS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            workTag,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )

        CoroutineScope(dispatcher).launch {
            workManager
                .getWorkInfosForUniqueWorkLiveData(workTag)
                .asFlow()
                .flowOn(dispatcher)
                .collect { works ->
                    val data = works[0].progress
                    if (data.keyValueMap.isNotEmpty()) {
                        val weather = weatherUpdateConverter.convertToWeather(data)
                        _weatherUpdates.emit(weather)
                    }
                }
        }

    }

    override val weatherUpdates: Flow<Weather> = _weatherUpdates.asSharedFlow()

    fun getCurrentRequest() = periodicWorkRequest
}