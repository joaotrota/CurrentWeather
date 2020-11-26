package com.joaoreis.currentweather

import android.app.Application
import com.joaoreis.currentweather.di.CurrentWeatherComponent
import com.joaoreis.currentweather.di.CurrentWeatherComponentImpl
import com.joaoreis.currentweather.di.DependencyContainer
import com.joaoreis.currentweather.di.DependencyProvider

class InstrumentedTestApplication : Application(), DependencyProvider {

    lateinit var dependencyContainer: DependencyContainer

    override val currentWeatherComponent: CurrentWeatherComponent
        get() = CurrentWeatherComponentImpl(dependencyContainer.weatherInteractor)
}