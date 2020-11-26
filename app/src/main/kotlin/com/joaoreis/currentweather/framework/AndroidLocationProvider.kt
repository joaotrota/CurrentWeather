package com.joaoreis.currentweather.framework

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.joaoreis.currentweather.DataResult
import com.joaoreis.currentweather.currentweather.LocationData
import com.joaoreis.currentweather.currentweather.LocationProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidLocationProvider(
    private val fusedLocationProvider: FusedLocationProviderClient,
    private val dispatcher: CoroutineDispatcher
) : LocationProvider {

    @SuppressLint("MissingPermission")
    override suspend fun getLocationData(): DataResult<LocationData> = withContext(dispatcher) {

        val location : Location? = suspendCoroutine { cont ->
            fusedLocationProvider.lastLocation.addOnCompleteListener {
                cont.resume(it.result)
            }
        }

        return@withContext if (location != null) {
            DataResult.Success(LocationData(location.latitude, location.longitude))
        } else {
            DataResult.Error<LocationData>()
        }
    }
}
