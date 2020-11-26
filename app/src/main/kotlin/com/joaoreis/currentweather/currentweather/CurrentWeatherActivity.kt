package com.joaoreis.currentweather.currentweather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.app.ActivityCompat
import androidx.transition.Visibility
import androidx.viewbinding.ViewBinding
import com.joaoreis.currentweather.R
import com.joaoreis.currentweather.databinding.CurrentWeatherActivityBinding
import com.joaoreis.currentweather.di.DependencyProvider

class CurrentWeatherActivity : AppCompatActivity(), WeatherView {

    private lateinit var presenter: WeatherPresenter

    private val viewBinding by viewBinding(CurrentWeatherActivityBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter =
            (applicationContext as DependencyProvider).currentWeatherComponent.weatherPresenter
        setContentView(viewBinding.root)

        if (!hasAllPermissions()) {
            requestAllPermissions()
        } else {
            presenter.loadWeather()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val allPermissionsGranted =
            mapPermissionsAndResults(permissions, grantResults).filter { it.value != 0 }.isEmpty()

        if (allPermissionsGranted) {
            presenter.loadWeather()
        } else {
            requestAllPermissions()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onPause() {
        presenter.detachView()
        super.onPause()
    }

    override fun onDestroy() {
        presenter.onViewDestroyed()
        super.onDestroy()
    }

    override fun showLoading() {
        with(viewBinding) {
            contentGroup.visibility = GONE
            errorView.visibility = GONE
            loadingView.visibility = VISIBLE
        }

    }

    override fun showError() {
        with(viewBinding) {
            contentGroup.visibility = GONE
            errorView.visibility = VISIBLE
            loadingView.visibility = GONE
        }
    }

    override fun showWeather(weather: Weather) {
        with(weather) {
            viewBinding.currentWeather.text = description
            viewBinding.currentTemperature.text =
                getString(R.string.current_temperature, temperature.toString())
            viewBinding.feelsLike.text = getString(R.string.feels_like, feelsLike.toString())
            viewBinding.minimumTemperature.text =
                getString(R.string.minimum_temperature, minimumTemperature.toString())
            viewBinding.maximumTemperature.text =
                getString(R.string.maximum_temperature, maximumTemperature.toString())
            viewBinding.pressure.text = getString(R.string.pressure, pressure.toString())
            viewBinding.humidity.text = getString(R.string.humidity, humidity.toString())
        }

        with(viewBinding) {
            contentGroup.visibility = VISIBLE
            errorView.visibility = GONE
            loadingView.visibility = GONE
        }
    }

    private inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
        crossinline bindingInflater: (LayoutInflater) -> T
    ) =
        lazy(LazyThreadSafetyMode.NONE) {
            bindingInflater.invoke(layoutInflater)
        }

    private fun mapPermissionsAndResults(
        permissions: Array<out String>,
        grantResults: IntArray
    ): Map<String, Int> =
        permissions.mapIndexedTo(mutableListOf()) { index, permission -> permission to grantResults[index] }
            .toMap()

    private fun hasAllPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        } else {
            (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        }
    }

    private fun requestAllPermissions() {

        val permissions: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        ActivityCompat.requestPermissions(
            this,
            permissions,
            1000
        )
    }
}