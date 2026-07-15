package com.samchendev.weathertest.domain.repos

import com.samchendev.weathertest.domain.models.WeatherInfo

interface WeatherRepo {
    suspend fun getWeatherInfo(city: String): Result<WeatherInfo>

    suspend fun getWeatherInfo(lat: Double, lon: Double): Result<WeatherInfo>
}