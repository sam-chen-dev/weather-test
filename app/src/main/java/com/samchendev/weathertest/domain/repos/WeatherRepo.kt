package com.samchendev.weathertest.domain.repos

import com.samchendev.weathertest.domain.models.WeatherInfo

interface WeatherRepo {
    suspend fun getWeatherInfo(city: String): WeatherInfo?

    suspend fun getWeatherInfo(lat: Double, lon: Double): WeatherInfo?
}