package com.joaoreis.currentweather

sealed class State<T> {
    class Idle<T> : State<T>()
    data class Loaded<T>(val data : T) : State<T>()
    class Error<T> : State<T>()
    class Loading<T> : State<T>()
}