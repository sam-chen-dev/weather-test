package com.samchendev.weathertest.models.networkModels

import com.samchendev.weathertest.models.domainModels.WeatherInfo
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val name: String
) {
    fun toWeatherInfo() = WeatherInfo(
        description = weather[0].description,
        iconUrl = getIconUrl(weather[0].icon),
        temperature = main.temp,
        feelsLike = main.feelsLike,
        minTemperature = main.tempMin,
        maxTemperature = main.tempMax,
        pressure = main.pressure,
        humidity = main.humidity,
        visibility = visibility,
        windSpeed = wind.speed,
        location = name
    )

    private fun getIconUrl(icon: String) = "https://openweathermap.org/payload/api/media/file/$icon.png"
}