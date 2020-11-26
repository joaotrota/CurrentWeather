package com.joaoreis.currentweather

import com.joaoreis.currentweather.currentweather.Weather
import com.joaoreis.currentweather.currentweather.WeatherPresenter
import com.joaoreis.currentweather.currentweather.WeatherView
import io.mockk.Called
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherPresenterTest {

    @Test
    fun `Given weather state is loading When view is attached Then show loading state in view`() =
        runBlockingTest {
            val testDispatcher = TestCoroutineDispatcher()

            val weatherInteractor = FakeWeatherInteractor()

            val view = spyk<WeatherView>()

            val presenter = WeatherPresenter(weatherInteractor, testDispatcher)

            presenter.attachView(view)

            verify {
                view.showLoading()
            }
        }

    @Test
    fun `Given weather state is loaded with a weather object When view is attached Then show weather state in view`() =
        runBlockingTest {
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
            val weatherInteractor = FakeWeatherInteractor(State.Loaded(weather))

            val view = spyk<WeatherView>()

            val presenter = WeatherPresenter(weatherInteractor, testDispatcher)

            presenter.attachView(view)

            verify {
                view.showWeather(weather)
            }
        }

    @Test
    fun `Given weather state is error When view is attached Then show error state in view`() =
        runBlockingTest {
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
            val weatherInteractor = FakeWeatherInteractor(State.Error())

            val view = spyk<WeatherView>()

            val presenter = WeatherPresenter(weatherInteractor, testDispatcher)

            presenter.attachView(view)

            verify {
                view.showError()
            }
        }

    @Test
    fun `Given any weather state When view is not attached Then view should not be called`() =
        runBlockingTest {
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
            val weatherInteractor = FakeWeatherInteractor(State.Error())

            val view = spyk<WeatherView>()

            val presenter = WeatherPresenter(weatherInteractor, testDispatcher)

            presenter.detachView()

            verify {
                view wasNot Called
            }
        }
}