package com.samchendev.weathertest.viewModels

import com.samchendev.weathertest.features.weatherSearch.WeatherSearchViewModel
import com.samchendev.weathertest.managers.cityManager.CityManager
import com.samchendev.weathertest.managers.cityManager.CityStorage
import com.samchendev.weathertest.models.networkModels.Main
import com.samchendev.weathertest.models.networkModels.Weather
import com.samchendev.weathertest.models.networkModels.WeatherResponse
import com.samchendev.weathertest.models.networkModels.Wind
import com.samchendev.weathertest.repos.WeatherRepoImpl
import com.samchendev.weathertest.services.WeatherApi
import com.samchendev.weathertest.utils.Constants
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class WeatherSearchViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init loads weather for last saved city`() = runTest {
        val cityStorage = FakeCityStorage("New York")
        val cityManager = CityManager(cityStorage)

        val weatherApi = FakeWeatherApi()
        val weatherRepo = WeatherRepoImpl(weatherApi)

        val viewModel = WeatherSearchViewModel(weatherRepo, cityManager)        // Trigger the init

        val weatherInfo = viewModel.uiState.value.weatherInfo

        assertEquals("New York", weatherApi.requestedCity)
        assertEquals("New York", weatherInfo?.location)
        assertEquals("broken clouds", weatherInfo?.description)
        assertEquals(23.45, weatherInfo?.temperature ?: 0.0, 0.0)
    }

    private class FakeCityStorage(private val savedCity: String) : CityStorage {
        override suspend fun saveCity(key: String, value: String) {}

        override fun getCity(key: String): String? {
            return if (key == Constants.CITY_KEY) savedCity else null
        }
    }

    private class FakeWeatherApi : WeatherApi {
        var requestedCity: String? = null

        override suspend fun getWeather(city: String): Response<WeatherResponse> {
            requestedCity = city
            return Response.success(createWeatherResponse(city))
        }

        override suspend fun getWeather(lat: Double, lon: Double): Response<WeatherResponse> {
            throw NotImplementedError("Not needed for this test")
        }
    }
}

private fun createWeatherResponse(city: String) = WeatherResponse(
    weather = listOf(
        Weather(
            id = 500,
            main = "Clear",
            description = "broken clouds",
            icon = "01d"
        )
    ),
    main = Main(
        temp = 23.45,
        feelsLike = 24.0,
        tempMin = 21.0,
        tempMax = 26.0,
        pressure = 1100,
        humidity = 65,
        seaLevel = 1000,
        grndLevel = 1200
    ),
    visibility = 10000,
    wind = Wind(
        speed = 3.5,
        deg = 200
    ),
    name = city
)