package com.joaoreis.currentweather.di

import com.joaoreis.currentweather.currentweather.WeatherPresenter

interface CurrentWeatherComponent {
    val weatherPresenter: WeatherPresenter
}