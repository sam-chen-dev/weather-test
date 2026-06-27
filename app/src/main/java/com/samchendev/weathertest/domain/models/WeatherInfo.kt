package com.samchendev.weathertest.domain.models

data class WeatherInfo(
    val description: String,
    val iconUrl: String,
    val temperature: Double,
    val feelsLike: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val pressure: Int,
    val humidity: Int,
    val visibility: Int,
    val windSpeed: Double,
    val location: String
)