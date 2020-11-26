package com.joaoreis.currentweather

import app.cash.turbine.test
import com.example.sandbox.FakeWeatherGateway
import com.joaoreis.currentweather.currentweather.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class CurrentWeatherInteractorImplTest {
    @Test
    fun `Given there is local weather information When weather is loaded Then should emit LOADED with local weather info`() = runBlockingTest {
        val testDispatcher = TestCoroutineDispatcher()

        val weather = Weather(
            "Clear",
            22.toDouble(),
            23.toDouble(),
            10.toDouble(),
            25.toDouble(),
            1000,
            99
        )

        val weatherManager = CurrentWeatherInteractorImpl(
            dispatcher = testDispatcher,
            weatherGateway = FakeWeatherGateway(),
            weatherPersistence = FakeWeatherPersistence(weather),
            periodicWeatherUpdater = mockk(relaxed = true),
            locationProvider = FakeLocationProvider()
        )

        weatherManager.currentWeather.test {
            assert(expectItem() is State.Idle)

            weatherManager.loadWeather()

            assert(expectItem() is State.Loading)
            assertEquals(State.Loaded(weather), expectItem())
        }
    }

    @Test
    fun `Given there is no local weather info When weather is loaded And weather gateway is working And location is obtained Then should emit LOADING And LOADED with remote weather info And persist it`() = runBlockingTest {
        val testDispatcher = TestCoroutineDispatcher()

        val weatherPersistence = mockk<WeatherPersistence>(relaxed = true).also {
            every { it.getCurrentWeather() } returns DataResult.Error()
        }

        val weather = Weather(
            "Clear",
            22.toDouble(),
            23.toDouble(),
            10.toDouble(),
            25.toDouble(),
            1000,
            99
        )

        val weatherManager = CurrentWeatherInteractorImpl(
            dispatcher = testDispatcher,
            weatherGateway = FakeWeatherGateway(weather),
            weatherPersistence = weatherPersistence,
            periodicWeatherUpdater = mockk(relaxed = true),
            locationProvider = FakeLocationProvider(LocationData(1.0,2.0))
        )

        weatherManager.currentWeather.test {
            assert(expectItem() is State.Idle)

            weatherManager.loadWeather()

            assert(expectItem() is State.Loading)
            assertEquals(State.Loaded(weather), expectItem())
        }

        verify {
            weatherPersistence.saveWeather(weather)
        }
    }

    @Test
    fun `Given there is no local weather info When weather is loaded And weather gateway is not working And location is obtained Then should emit ERROR`() = runBlockingTest {
        val testDispatcher = TestCoroutineDispatcher()

        val weather = Weather(
            "Clear",
            22.toDouble(),
            23.toDouble(),
            10.toDouble(),
            25.toDouble(),
            1000,
            99
        )

        val weatherManager = CurrentWeatherInteractorImpl(
            dispatcher = testDispatcher,
            weatherGateway = FakeWeatherGateway(),
            weatherPersistence = FakeWeatherPersistence(),
            periodicWeatherUpdater = mockk(relaxed = true),
            locationProvider = FakeLocationProvider(LocationData(1.0,2.0))
        )

        weatherManager.currentWeather.test {
            assert(expectItem() is State.Idle)

            weatherManager.loadWeather()

            assert(expectItem() is State.Loading)
            assert(expectItem() is State.Error)
        }
    }

    @Test
    fun `Given there is no local weather info When weather is loaded And weather gateway is working And location fails Then should emit ERROR`() = runBlockingTest {
        val testDispatcher = TestCoroutineDispatcher()

        val weather = Weather(
            "Clear",
            22.toDouble(),
            23.toDouble(),
            10.toDouble(),
            25.toDouble(),
            1000,
            99
        )

        val weatherManager = CurrentWeatherInteractorImpl(
            dispatcher = testDispatcher,
            weatherGateway = FakeWeatherGateway(weather),
            weatherPersistence = FakeWeatherPersistence(),
            periodicWeatherUpdater = mockk(relaxed = true),
            locationProvider = FakeLocationProvider()
        )

        weatherManager.currentWeather.test {
            assert(expectItem() is State.Idle)

            weatherManager.loadWeather()

            assert(expectItem() is State.Loading)
            assert(expectItem() is State.Error)
        }
    }

    @Test
    fun `Given weather was updated successfully When update is received Then emit LOADED state with the value`() = runBlockingTest {
        val testDispatcher = TestCoroutineDispatcher()

        val weather = Weather(
            "Clear",
            22.toDouble(),
            23.toDouble(),
            10.toDouble(),
            25.toDouble(),
            1000,
            99
        )

        val weatherManager = CurrentWeatherInteractorImpl(
            dispatcher = testDispatcher,
            weatherGateway = FakeWeatherGateway(),
            weatherPersistence = FakeWeatherPersistence(),
            periodicWeatherUpdater = FakePeriodicWeatherUpdater(weather, testDispatcher),
            locationProvider = FakeLocationProvider(LocationData(1.0,2.0))
        )

        weatherManager.currentWeather.test {
            assertEquals(State.Loaded(weather), expectItem())
        }
    }

    @Test
    fun `Given a periodic weather updater When weather interactor starts Then should setup updates on updater`() = runBlockingTest {
        val testDispatcher = TestCoroutineDispatcher()

        val periodicWeatherUpdater = mockk<PeriodicWeatherUpdater>(relaxed = true)
        every { periodicWeatherUpdater.weatherUpdates } returns MutableSharedFlow()

        CurrentWeatherInteractorImpl(
            dispatcher = testDispatcher,
            weatherGateway = FakeWeatherGateway(),
            weatherPersistence = FakeWeatherPersistence(),
            periodicWeatherUpdater = periodicWeatherUpdater,
            locationProvider = FakeLocationProvider(LocationData(1.0,2.0))
        )

        verify {
            periodicWeatherUpdater.startUpdates()
        }
    }
}