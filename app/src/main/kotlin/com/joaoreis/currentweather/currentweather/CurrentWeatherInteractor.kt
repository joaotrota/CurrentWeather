package com.joaoreis.currentweather.currentweather

import com.joaoreis.currentweather.State
import kotlinx.coroutines.flow.StateFlow

interface CurrentWeatherInteractor {
    suspend fun loadWeather()
    fun setupPeriodicUpdates()
    val currentWeather: StateFlow<State<Weather>>
}