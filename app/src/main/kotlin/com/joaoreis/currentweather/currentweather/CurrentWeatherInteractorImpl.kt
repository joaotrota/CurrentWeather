package com.joaoreis.currentweather.currentweather

import com.joaoreis.currentweather.DataResult
import com.joaoreis.currentweather.State
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CurrentWeatherInteractorImpl(
    private val dispatcher: CoroutineDispatcher,
    private val weatherGateway: WeatherGateway,
    private val weatherPersistence: WeatherPersistence,
    private val periodicWeatherUpdater: PeriodicWeatherUpdater,
    private val locationProvider: LocationProvider
) : CurrentWeatherInteractor {

    private val _currentWeather = MutableStateFlow<State<Weather>>(State.Idle())

    init {
        setupPeriodicUpdates()
    }

    override fun setupPeriodicUpdates() {
        periodicWeatherUpdater.startUpdates()
        CoroutineScope(dispatcher).launch {
            periodicWeatherUpdater
                .weatherUpdates
                .collect {
                    _currentWeather.emit(State.Loaded(it))
                }
        }
    }

    override suspend fun loadWeather() {
        withContext(dispatcher) {

            _currentWeather.emit(State.Loading())

            when (val localData = weatherPersistence.getCurrentWeather()) {
                is DataResult.Success -> {
                    _currentWeather.emit(State.Loaded(localData.data))
                }
                is DataResult.Error -> {

                    when (val location = locationProvider.getLocationData()) {
                        is DataResult.Success -> {
                            when (val remoteData = weatherGateway.getCurrentWeather(location.data.latitude, location.data.longitude)) {
                                is DataResult.Success -> {
                                    weatherPersistence.saveWeather(remoteData.data)

                                    _currentWeather.emit(State.Loaded(remoteData.data))
                                }
                                is DataResult.Error -> _currentWeather.emit(State.Error())
                            }
                        }
                        is DataResult.Error -> _currentWeather.emit(State.Error())
                    }
                }
            }
        }
    }

    override val currentWeather: StateFlow<State<Weather>> = _currentWeather.asStateFlow()
}