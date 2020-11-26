package com.joaoreis.currentweather.currentweather

import com.joaoreis.currentweather.DataResult

interface LocationProvider {
    suspend fun getLocationData() : DataResult<LocationData>
}