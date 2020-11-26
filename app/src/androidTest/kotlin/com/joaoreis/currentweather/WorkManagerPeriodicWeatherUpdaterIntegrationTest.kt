package com.joaoreis.currentweather

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import app.cash.turbine.test
import com.example.sandbox.FakeWeatherGateway
import com.joaoreis.currentweather.currentweather.data.workmanager.WeatherUpdateWorkerFactory
import com.joaoreis.currentweather.currentweather.LocationData
import com.joaoreis.currentweather.currentweather.Weather
import com.joaoreis.currentweather.currentweather.WeatherGateway
import com.joaoreis.currentweather.currentweather.WeatherPersistence
import com.joaoreis.currentweather.currentweather.data.workmanager.WorkManagerPeriodicWeatherUpdater
import com.joaoreis.currentweather.currentweather.data.workmanager.WorkManagerWeatherUpdateConverter
import io.mockk.Called
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@RunWith(AndroidJUnit4::class)
class WorkManagerPeriodicWeatherUpdaterIntegrationTest {

    private var weatherPersistence: WeatherPersistence = mockk(relaxed = true)
    private var weatherGateway: WeatherGateway = FakeWeatherGateway()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun shouldEmitWeatherUpdateAndSaveLocallyWhenDelayHasPassedAndDeviceHasUnmeteredConnection() = runBlocking {
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

        weatherGateway = FakeWeatherGateway(weather)

        val context: Context = getApplicationContext()
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(
                WeatherUpdateWorkerFactory(
                    weatherGateway,
                    weatherPersistence,
                    WorkManagerWeatherUpdateConverter(),
                    FakeLocationProvider(LocationData(1.0,2.0))
                )
            )
            .setExecutor(SynchronousExecutor())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)

        val workManager = WorkManager.getInstance(context)
        val workTag = "WorkTag"


        val workManagerPeriodicWeatherUpdater =
            WorkManagerPeriodicWeatherUpdater(
                workManager,
                workTag,
                testDispatcher,
                WorkManagerWeatherUpdateConverter()
            )

        workManagerPeriodicWeatherUpdater.startUpdates()

        testDriver!!.setInitialDelayMet(workManagerPeriodicWeatherUpdater.getCurrentRequest().id)
        testDriver.setPeriodDelayMet(workManagerPeriodicWeatherUpdater.getCurrentRequest().id)
        testDriver.setAllConstraintsMet(workManagerPeriodicWeatherUpdater.getCurrentRequest().id)

        workManagerPeriodicWeatherUpdater.weatherUpdates.test {
            assertEquals(weather, expectItem())
        }

        verify {
            weatherPersistence.saveWeather(weather)
        }
    }

    @Test
    fun shouldNotEmitWeatherUpdateAndNotSaveLocallyWhenDelayHasPassedAndDeviceDoesNotHaveUnmeteredConnection() = runBlocking {
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

        weatherGateway = FakeWeatherGateway(weather)

        val context: Context = getApplicationContext()
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(
                WeatherUpdateWorkerFactory(
                    weatherGateway,
                    weatherPersistence,
                    WorkManagerWeatherUpdateConverter(),
                    FakeLocationProvider(LocationData(1.0,2.0))
                )
            )
            .setExecutor(SynchronousExecutor())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        val workManager = WorkManager.getInstance(context)
        val workTag = "WorkTag"


        val workManagerPeriodicWeatherUpdater =
            WorkManagerPeriodicWeatherUpdater(
                workManager,
                workTag,
                testDispatcher,
                WorkManagerWeatherUpdateConverter()
            )


        workManagerPeriodicWeatherUpdater.startUpdates()

        workManagerPeriodicWeatherUpdater.weatherUpdates.test {
            //Intentionally left blank since this is the way to test that no items were emitted
        }

        verify {
            weatherPersistence wasNot Called
        }
    }

    @Test
    fun shouldNotEmitWeatherUpdateAndNotSaveLocallyWhenDelayHasPassedAndDeviceDoesHasUnmeteredConnectionAndLocationCouldNotBeObtained() = runBlocking {
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

        weatherGateway = FakeWeatherGateway(weather)

        val context: Context = getApplicationContext()
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(
                WeatherUpdateWorkerFactory(
                    weatherGateway,
                    weatherPersistence,
                    WorkManagerWeatherUpdateConverter(),
                    FakeLocationProvider()
                )
            )
            .setExecutor(SynchronousExecutor())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)

        val workManager = WorkManager.getInstance(context)
        val workTag = "WorkTag"


        val workManagerPeriodicWeatherUpdater =
            WorkManagerPeriodicWeatherUpdater(
                workManager,
                workTag,
                testDispatcher,
                WorkManagerWeatherUpdateConverter()
            )


        workManagerPeriodicWeatherUpdater.startUpdates()

        testDriver!!.setInitialDelayMet(workManagerPeriodicWeatherUpdater.getCurrentRequest().id)
        testDriver.setPeriodDelayMet(workManagerPeriodicWeatherUpdater.getCurrentRequest().id)
        testDriver.setAllConstraintsMet(workManagerPeriodicWeatherUpdater.getCurrentRequest().id)

        workManagerPeriodicWeatherUpdater.weatherUpdates.test {
            //Intentionally left blank since this is the way to test that no items were emitted
        }

        verify {
            weatherPersistence wasNot Called
        }
    }
}

