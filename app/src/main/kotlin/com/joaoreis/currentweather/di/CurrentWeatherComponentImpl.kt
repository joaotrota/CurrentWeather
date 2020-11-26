package com.joaoreis.currentweather.di

import com.joaoreis.currentweather.currentweather.CurrentWeatherInteractor
import com.joaoreis.currentweather.currentweather.WeatherPresenter
import kotlinx.coroutines.Dispatchers

class CurrentWeatherComponentImpl(
    private val weatherInteractor: CurrentWeatherInteractor
) : CurrentWeatherComponent {
    override val weatherPresenter: WeatherPresenter
        get() = WeatherPresenter(weatherInteractor, Dispatchers.Main)
}