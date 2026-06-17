package com.samchendev.weathertest.repos

import com.samchendev.weathertest.models.networkModels.Main
import com.samchendev.weathertest.models.networkModels.Weather
import com.samchendev.weathertest.models.networkModels.WeatherResponse
import com.samchendev.weathertest.models.networkModels.Wind
import com.samchendev.weathertest.services.WeatherApi
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.Response

class WeatherRepoImplTest {
    val weatherResponse = createWeatherResponse()

    @Test
    fun `getWeatherInfo(city) returns weather info when api succeeds`() = runBlocking {
        val weatherApi = object : WeatherApi {
            override suspend fun getWeather(city: String): Response<WeatherResponse> {
                return Response.success(weatherResponse)
            }

            override suspend fun getWeather(lat: Double, lon: Double): Response<WeatherResponse> {
                throw NotImplementedError("Not needed for this test")
            }
        }

        val weatherRepo = WeatherRepoImpl(weatherApi)
        val weatherInfo = weatherRepo.getWeatherInfo("New York")

        assertEquals("New York", weatherInfo?.location)
        assertEquals("broken clouds", weatherInfo?.description)
        assertEquals("https://openweathermap.org/payload/api/media/file/01d.png", weatherInfo?.iconUrl)
        assertEquals(23.45, weatherInfo?.temperature ?: 0.0, 0.0)
        assertEquals(24.0, weatherInfo?.feelsLike ?: 0.0, 0.0)
        assertEquals(21.0, weatherInfo?.minTemperature ?: 0.0, 0.0)
        assertEquals(26.0, weatherInfo?.maxTemperature ?: 0.0, 0.0)
        assertEquals(1100, weatherInfo?.pressure)
        assertEquals(65, weatherInfo?.humidity)
        assertEquals(10000, weatherInfo?.visibility)
        assertEquals(3.5, weatherInfo?.windSpeed ?: 0.0, 0.0)
    }

    @Test
    fun `getWeatherInfo(city) throws exception when api fails`() {
        val errorMessage = "{\"message\": \"City not found\"}"
        val weatherApi = object : WeatherApi {
            override suspend fun getWeather(city: String): Response<WeatherResponse> {
                val errorBody = errorMessage.toResponseBody("application/json".toMediaType())

                return Response.error(404, errorBody)
            }

            override suspend fun getWeather(lat: Double, lon: Double): Response<WeatherResponse> {
                throw NotImplementedError("Not needed for this test")
            }
        }

        val weatherRepo = WeatherRepoImpl(weatherApi)
        val exception = assertThrows(Exception::class.java) {
            runBlocking {
                weatherRepo.getWeatherInfo("abcd")      //invalid city name
            }
        }

        assertEquals(errorMessage, exception.message)
    }

    @Test
    fun `getWeatherInfo(lat, lon) returns weather info when api succeeds`() = runBlocking {
        val weatherApi = object : WeatherApi {
            override suspend fun getWeather(city: String): Response<WeatherResponse> {
                throw NotImplementedError("Not needed for this test")
            }

            override suspend fun getWeather(lat: Double, lon: Double): Response<WeatherResponse> {
                return Response.success(weatherResponse)
            }
        }

        val weatherRepo = WeatherRepoImpl(weatherApi)
        val weatherInfo = weatherRepo.getWeatherInfo(40.7128, -74.0060)

        assertEquals("New York", weatherInfo?.location)
        assertEquals("broken clouds", weatherInfo?.description)
        assertEquals("https://openweathermap.org/payload/api/media/file/01d.png", weatherInfo?.iconUrl)
        assertEquals(23.45, weatherInfo?.temperature ?: 0.0, 0.0)
        assertEquals(24.0, weatherInfo?.feelsLike ?: 0.0, 0.0)
        assertEquals(21.0, weatherInfo?.minTemperature ?: 0.0, 0.0)
        assertEquals(26.0, weatherInfo?.maxTemperature ?: 0.0, 0.0)
        assertEquals(1100, weatherInfo?.pressure)
        assertEquals(65, weatherInfo?.humidity)
        assertEquals(10000, weatherInfo?.visibility)
        assertEquals(3.5, weatherInfo?.windSpeed ?: 0.0, 0.0)
    }

    @Test
    fun `getWeatherInfo(lat, lon) throws exception when api fails`() {
        val errorMessage = "{\"message\": \"City not found\"}"
        val weatherApi = object : WeatherApi {
            override suspend fun getWeather(city: String): Response<WeatherResponse> {
                throw NotImplementedError("Not needed for this test")
            }

            override suspend fun getWeather(lat: Double, lon: Double): Response<WeatherResponse> {
                val errorBody = errorMessage.toResponseBody("application/json".toMediaType())

                return Response.error(404, errorBody)
            }
        }

        val weatherRepo = WeatherRepoImpl(weatherApi)
        val exception = assertThrows(Exception::class.java) {
            runBlocking {
                weatherRepo.getWeatherInfo(105.4321, -45.1234)      //invalid lat, lon
            }
        }

        assertEquals(errorMessage, exception.message)
    }

    private fun createWeatherResponse() = WeatherResponse(
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
        name = "New York"
    )
}