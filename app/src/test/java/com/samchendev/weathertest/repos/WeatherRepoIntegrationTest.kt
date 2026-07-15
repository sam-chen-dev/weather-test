package com.samchendev.weathertest.repos

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.samchendev.weathertest.data.remote.WeatherApi
import com.samchendev.weathertest.data.remote.models.Main
import com.samchendev.weathertest.data.remote.models.Weather
import com.samchendev.weathertest.data.remote.models.WeatherResponse
import com.samchendev.weathertest.data.remote.models.Wind
import com.samchendev.weathertest.data.repos.WeatherRepoImpl
import com.samchendev.weathertest.domain.repos.WeatherRepo
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class WeatherRepoIntegrationTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var weatherApi: WeatherApi
    private lateinit var weatherRepo: WeatherRepo
    private val weatherResponse = createWeatherResponse()

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

        weatherApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(WeatherApi::class.java)

        weatherRepo = WeatherRepoImpl(weatherApi)
    }

    @After
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    fun `get weather success`() = runTest {
        val weatherResponseJson = Json.encodeToString<WeatherResponse>(weatherResponse)
        val mockResponse = MockResponse.Builder().run {
            code(200)
            body(weatherResponseJson)
            addHeader("Content-Type", "application/json")
            build()
        }

        mockWebServer.enqueue(mockResponse)

        val result = weatherRepo.getWeatherInfo("Boston").getOrThrow()
        assertEquals("Boston", result.location)
        assertEquals("broken clouds", result.description)
        assertEquals(23.45, result.temperature, 0.0)

        val request = mockWebServer.takeRequest()
        assertEquals("/weather?q=Boston", request.target)
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
        name = "Boston"
    )
}