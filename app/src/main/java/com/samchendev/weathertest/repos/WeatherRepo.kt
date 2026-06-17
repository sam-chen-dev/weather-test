package com.samchendev.weathertest.repos

import com.samchendev.weathertest.models.domainModels.WeatherInfo

interface WeatherRepo {
    suspend fun getWeatherInfo(city: String): WeatherInfo?

    suspend fun getWeatherInfo(lat: Double, lon: Double): WeatherInfo?
}