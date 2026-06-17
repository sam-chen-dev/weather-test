package com.samchendev.weathertest.networkModels

import com.samchendev.weathertest.models.networkModels.Main
import com.samchendev.weathertest.models.networkModels.Weather
import com.samchendev.weathertest.models.networkModels.WeatherResponse
import com.samchendev.weathertest.models.networkModels.Wind
import org.junit.Assert
import org.junit.Test

class WeatherResponseTest {
    @Test
    fun `toWeatherInfo() maps weather response to domain model correctly`() {
        val weatherResponse = WeatherResponse(
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

        val weatherInfo = weatherResponse.toWeatherInfo()

        Assert.assertEquals("New York", weatherInfo.location)
        Assert.assertEquals("broken clouds", weatherInfo.description)
        Assert.assertEquals("https://openweathermap.org/payload/api/media/file/01d.png", weatherInfo.iconUrl)
        Assert.assertEquals(23.45, weatherInfo.temperature, 0.0)
        Assert.assertEquals(24.0, weatherInfo.feelsLike, 0.0)
        Assert.assertEquals(21.0, weatherInfo.minTemperature, 0.0)
        Assert.assertEquals(26.0, weatherInfo.maxTemperature, 0.0)
        Assert.assertEquals(1100, weatherInfo.pressure)
        Assert.assertEquals(65, weatherInfo.humidity)
        Assert.assertEquals(10000, weatherInfo.visibility)
        Assert.assertEquals(3.5, weatherInfo.windSpeed, 0.0)
    }
}