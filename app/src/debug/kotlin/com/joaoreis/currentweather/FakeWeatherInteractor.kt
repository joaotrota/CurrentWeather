package com.joaoreis.currentweather

import com.joaoreis.currentweather.currentweather.CurrentWeatherInteractor
import com.joaoreis.currentweather.currentweather.Weather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeWeatherInteractor(
    currentState: State<Weather> = State.Loading()
) : CurrentWeatherInteractor {

    private val _state = MutableStateFlow(currentState)

    override suspend fun loadWeather() {
    }

    override fun setupPeriodicUpdates() {
    }

    override val currentWeather: StateFlow<State<Weather>> = _state
}
