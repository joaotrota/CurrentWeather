package com.joaoreis.currentweather

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.testing.WorkManagerTestInitHelper
import com.joaoreis.currentweather.currentweather.CurrentWeatherActivity
import com.joaoreis.currentweather.currentweather.LocationData
import com.joaoreis.currentweather.currentweather.data.workmanager.WorkManagerPeriodicWeatherUpdater
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CurrentWeatherAcceptanceTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun Given_there_is_local_weather_data_When_current_weather_screen_is_opened_Then_should_show_local_weather_data() {
        val application = ApplicationProvider.getApplicationContext<InstrumentedTestApplication>()

        val sharedPreferences = application.getSharedPreferences(
            "WEATHER_PREF",
            Context.MODE_PRIVATE
        )

        sharedPreferences.edit().putString("WEATHER_KEY", TestData.STORED_WEATHER_JSON).commit()

        application.dependencyContainer = TestDependencyContainer(
            okHttpClient = OkHttpClient().newBuilder().build(),
            sharedPreferences = sharedPreferences,
            baseUrl = "/",
            apiKey = "123",
            applicationContext = application
        )

        val scenario = launch(CurrentWeatherActivity::class.java)

        onView(withId(R.id.current_weather)).check(
            matches(withText("clear sky"))
        )

        onView(withId(R.id.current_temperature)).check(
            matches(withText("Current temperature: 282.55 ºC"))
        )

        onView(withId(R.id.feels_like)).check(
            matches(withText("Feels like: 281.86 ºC"))
        )

        onView(withId(R.id.minimum_temperature)).check(
            matches(withText("Min. temperature: 280.37 ºC"))
        )

        onView(withId(R.id.maximum_temperature)).check(
            matches(withText("Max. temperature: 284.26 ºC"))
        )

        onView(withId(R.id.pressure)).check(
            matches(withText("Pressure: 1023"))
        )

        onView(withId(R.id.humidity)).check(
            matches(withText("Humidity: 100 %"))
        )

        scenario.close()
    }

    @Test
    fun Given_there_is_no_local_weather_data_When_current_weather_screen_is_opened_Then_should_show_weather_data_from_API() {
        val server = MockWebServer()
        server.start()
        val baseUrl = server.url("/").toString()

        val application = ApplicationProvider.getApplicationContext<InstrumentedTestApplication>()

        val sharedPreferences = application.getSharedPreferences(
            "WEATHER_PREF",
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit().clear().commit()

        server.enqueue(MockResponse().setResponseCode(200).setBody(TestData.WEATHER_JSON))

        application.dependencyContainer = TestDependencyContainer(
            okHttpClient = OkHttpClient().newBuilder().build(),
            sharedPreferences = sharedPreferences,
            baseUrl = baseUrl,
            apiKey = "123",
            applicationContext = application,
            locationData = LocationData(33.0, 159.0)
        )

        val scenario = launch(CurrentWeatherActivity::class.java)

        //Weird behaviour :S If i don't wait a bit the test fails.
        //Tried Espresso.onIdle() but it also fails.
        Thread.sleep(500)

        onView(withId(R.id.current_weather)).check(
            matches(withText("cloudy sky"))
        )

        onView(withId(R.id.current_temperature)).check(
            matches(withText("Current temperature: 182.55 ºC"))
        )

        onView(withId(R.id.feels_like)).check(
            matches(withText("Feels like: 181.86 ºC"))
        )

        onView(withId(R.id.minimum_temperature)).check(
            matches(withText("Min. temperature: 180.37 ºC"))
        )

        onView(withId(R.id.maximum_temperature)).check(
            matches(withText("Max. temperature: 184.26 ºC"))
        )

        onView(withId(R.id.pressure)).check(
            matches(withText("Pressure: 1923"))
        )

        onView(withId(R.id.humidity)).check(
            matches(withText("Humidity: 99 %"))
        )


        server.close()
        scenario.close()
    }

    @Test
    fun Given_there_is_no_local_weather_data_And_API_is_down_When_current_weather_screen_is_opened_Then_should_show_error() {
        val server = MockWebServer()
        server.start()
        val baseUrl = server.url("/").toString()

        val application = ApplicationProvider.getApplicationContext<InstrumentedTestApplication>()

        val sharedPreferences = application.getSharedPreferences(
            "WEATHER_PREF",
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit().clear().commit()

        server.enqueue(MockResponse().setResponseCode(400))

        application.dependencyContainer = TestDependencyContainer(
            okHttpClient = OkHttpClient().newBuilder().build(),
            sharedPreferences = sharedPreferences,
            baseUrl = baseUrl,
            apiKey = "123",
            applicationContext = application,
            locationData = LocationData(33.0, 159.0)
        )

        val scenario = launch(CurrentWeatherActivity::class.java)

        //Weird behaviour :S If i don't wait a bit the test fails.
        //Tried Espresso.onIdle() but it also fails.
        Thread.sleep(500)

        onView(withId(R.id.error_view)).check(
            matches(withEffectiveVisibility(Visibility.VISIBLE))
        )

        server.close()
        scenario.close()
    }

    @Test
    fun Given_there_is_weather_data_displayed_When_periodic_update_is_triggered_Then_should_show_update_data() {
        val server = MockWebServer()
        server.start()
        val baseUrl = server.url("/").toString()

        val application = ApplicationProvider.getApplicationContext<InstrumentedTestApplication>()

        val sharedPreferences = application.getSharedPreferences(
            "WEATHER_PREF",
            Context.MODE_PRIVATE
        )


        sharedPreferences.edit().putString("WEATHER_KEY", TestData.STORED_WEATHER_JSON).commit()

        server.enqueue(MockResponse().setResponseCode(200).setBody(TestData.WEATHER_JSON.trimIndent()))


        application.dependencyContainer = TestDependencyContainer(
            okHttpClient = OkHttpClient().newBuilder().build(),
            sharedPreferences = sharedPreferences,
            baseUrl = baseUrl,
            apiKey = "123",
            applicationContext = application,
            locationData = LocationData(1.0,2.0)
        )


        val scenario = launch(CurrentWeatherActivity::class.java)

        onView(withId(R.id.current_weather)).check(
            matches(withText("clear sky"))
        )

        onView(withId(R.id.current_temperature)).check(
            matches(withText("Current temperature: 282.55 ºC"))
        )

        onView(withId(R.id.feels_like)).check(
            matches(withText("Feels like: 281.86 ºC"))
        )

        onView(withId(R.id.minimum_temperature)).check(
            matches(withText("Min. temperature: 280.37 ºC"))
        )

        onView(withId(R.id.maximum_temperature)).check(
            matches(withText("Max. temperature: 284.26 ºC"))
        )

        onView(withId(R.id.pressure)).check(
            matches(withText("Pressure: 1023"))
        )

        onView(withId(R.id.humidity)).check(
            matches(withText("Humidity: 100 %"))
        )

        val testDriver = WorkManagerTestInitHelper.getTestDriver(application)
        val request =
            (application.dependencyContainer.periodicWeatherUpdater as WorkManagerPeriodicWeatherUpdater).getCurrentRequest()

        testDriver?.apply {
            setInitialDelayMet(request.id)
            setPeriodDelayMet(request.id)
            setAllConstraintsMet(request.id)
        }

        Thread.sleep(500)

        onView(withId(R.id.current_weather)).check(
            matches(withText("cloudy sky"))
        )

        onView(withId(R.id.current_temperature)).check(
            matches(withText("Current temperature: 182.55 ºC"))
        )

        onView(withId(R.id.feels_like)).check(
            matches(withText("Feels like: 181.86 ºC"))
        )

        onView(withId(R.id.minimum_temperature)).check(
            matches(withText("Min. temperature: 180.37 ºC"))
        )

        onView(withId(R.id.maximum_temperature)).check(
            matches(withText("Max. temperature: 184.26 ºC"))
        )

        onView(withId(R.id.pressure)).check(
            matches(withText("Pressure: 1923"))
        )

        onView(withId(R.id.humidity)).check(
            matches(withText("Humidity: 99 %"))
        )

        server.close()
        scenario.close()
    }
}