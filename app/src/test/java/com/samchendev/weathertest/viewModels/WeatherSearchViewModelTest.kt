package com.samchendev.weathertest.viewModels

import androidx.compose.foundation.text.input.setTextAndSelectAll
import com.samchendev.weathertest.R
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
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

    @Test
    fun `init does nothing when no city is saved`() = runTest {
        val cityStorage = FakeCityStorage(null)
        val cityManager = CityManager(cityStorage)

        val weatherApi = FakeWeatherApi()
        val weatherRepo = WeatherRepoImpl(weatherApi)

        val viewModel = WeatherSearchViewModel(weatherRepo, cityManager)

        assertEquals(null, weatherApi.requestedCity)
        assertEquals(null, viewModel.uiState.value.weatherInfo)
        assertEquals(false, viewModel.uiState.value.isProcessing)
    }

    @Test
    fun `search city updates weather info and saves city when api succeeds`() = runTest {
        val cityStorage = FakeCityStorage(null)     // Start with no saved city, avoiding init
        val cityManager = CityManager(cityStorage)

        val weatherApi = FakeWeatherApi()
        val weatherRepo = WeatherRepoImpl(weatherApi)

        val viewModel = WeatherSearchViewModel(weatherRepo, cityManager)

        viewModel.cityState.setTextAndSelectAll("Boston")
        viewModel.uiState.value.onSearchClick()

        val weatherInfo = viewModel.uiState.value.weatherInfo

        assertEquals("Boston", weatherApi.requestedCity)
        assertEquals("Boston", weatherInfo?.location)
        assertEquals("broken clouds", weatherInfo?.description)
        assertEquals(23.45, weatherInfo?.temperature ?: 0.0, 0.0)

        assertEquals("Boston", cityStorage.savedCity)
        assertEquals("Boston", cityManager.getCity())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `search invalid city throws exception and emit error message`() = runTest {
        val cityStorage = FakeCityStorage(null)
        val cityManager = CityManager(cityStorage)

        val weatherApi = FakeFailingWeatherApi()
        val weatherRepo = WeatherRepoImpl(weatherApi)

        val viewModel = WeatherSearchViewModel(weatherRepo, cityManager)

        viewModel.cityState.setTextAndSelectAll("abcd")     // Invalid city name

        val errorMessages = mutableListOf<Int>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.errorMessage.collect { errorMessages.add(it) }
        }

        viewModel.uiState.value.onSearchClick()

        assertEquals(false, viewModel.uiState.value.isProcessing)
        assertEquals(R.string.city_not_found_message, errorMessages.firstOrNull())
        assertEquals(null, viewModel.uiState.value.weatherInfo)

        assertEquals(null, cityStorage.savedCity)
        assertEquals(null, cityManager.getCity())
    }

    private class FakeCityStorage(var savedCity: String?) : CityStorage {
        override suspend fun saveCity(key: String, value: String) {
            savedCity = value
        }

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

    private class FakeFailingWeatherApi : WeatherApi {
        override suspend fun getWeather(city: String): Response<WeatherResponse> {
            val errorMessage = "{\"message\": \"City not found\"}"
            val errorBody = errorMessage.toResponseBody("application/json".toMediaType())
            return Response.error(404, errorBody)
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