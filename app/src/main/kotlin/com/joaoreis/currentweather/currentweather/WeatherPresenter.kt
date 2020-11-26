package com.joaoreis.currentweather.currentweather

import com.joaoreis.currentweather.State
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect

class WeatherPresenter(
    private val weatherInteractor: CurrentWeatherInteractor,
    coroutineDispatcher: CoroutineDispatcher
) {

    var view: WeatherView? = null

    private val coroutineScope = CoroutineScope(coroutineDispatcher + Job())

    fun loadWeather() {
        coroutineScope.launch {
            weatherInteractor.loadWeather()
        }
    }

    fun attachView(weatherView: WeatherView) {
        view = weatherView
        coroutineScope.launch {
            weatherInteractor
                .currentWeather
                .collect { state ->
                    when(state) {
                        is State.Idle -> {}
                        is State.Loaded -> {
                            view?.showWeather(state.data)
                        }
                        is State.Error -> view?.showError()
                        is State.Loading -> view?.showLoading()
                    }
                }
        }
    }

    fun detachView() {
        view = null
    }

    fun onViewDestroyed() {
        coroutineScope.cancel()
    }

}