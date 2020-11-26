package com.joaoreis.currentweather

import com.joaoreis.currentweather.currentweather.PeriodicWeatherUpdater
import com.joaoreis.currentweather.currentweather.Weather
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class FakePeriodicWeatherUpdater(
    private val weather: Weather? = null,
    dispatcher: CoroutineDispatcher
) : PeriodicWeatherUpdater {

    private val _weatherUpdates = MutableSharedFlow<Weather>(replay = 1)

    init {
        if (weather != null) {
            CoroutineScope(dispatcher).launch {
                _weatherUpdates.emit(weather)
            }
        }
    }

    override fun startUpdates() {

    }

    override val weatherUpdates: Flow<Weather> = _weatherUpdates
}