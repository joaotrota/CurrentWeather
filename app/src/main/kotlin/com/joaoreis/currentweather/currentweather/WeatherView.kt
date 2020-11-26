package com.joaoreis.currentweather.currentweather

interface WeatherView {
    fun showLoading()
    fun showError()
    fun showWeather(weather: Weather)
}