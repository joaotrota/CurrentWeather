package com.joaoreis.currentweather.di

import com.joaoreis.currentweather.currentweather.*
import com.joaoreis.currentweather.currentweather.data.http.WeatherJsonParser
import com.joaoreis.currentweather.currentweather.data.local.StoredWeatherConverter
import com.joaoreis.currentweather.currentweather.data.workmanager.WeatherUpdateConverter

interface DependencyContainer {
    val weatherUpdateConverter: WeatherUpdateConverter
    val weatherJsonParser: WeatherJsonParser
    val storedWeatherConverter: StoredWeatherConverter
    val periodicWeatherUpdater: PeriodicWeatherUpdater
    val weatherPersistence: WeatherPersistence
    val weatherGateway: WeatherGateway
    val locationProvider: LocationProvider
    val weatherInteractor: CurrentWeatherInteractor
}