package com.joaoreis.currentweather.currentweather

import kotlinx.coroutines.flow.Flow

interface PeriodicWeatherUpdater {
    fun startUpdates()
    val weatherUpdates: Flow<Weather>
}