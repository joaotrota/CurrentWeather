package com.joaoreis.currentweather

import com.joaoreis.currentweather.currentweather.LocationData
import com.joaoreis.currentweather.currentweather.LocationProvider

class FakeLocationProvider(
    private val locationData: LocationData? = null
) : LocationProvider {
    override suspend fun getLocationData(): DataResult<LocationData> {
        return if (locationData != null) {
            DataResult.Success(locationData)
        } else {
            DataResult.Error()
        }
    }
}