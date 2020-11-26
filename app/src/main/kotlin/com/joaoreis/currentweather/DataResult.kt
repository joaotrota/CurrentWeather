package com.joaoreis.currentweather

sealed class DataResult<R> {
    data class Success<R>(val data: R) : DataResult<R>()
    class Error<R> : DataResult<R>()
}