package com.samchendev.weathertest.data.repos

import com.samchendev.weathertest.domain.repos.WeatherRepo
import com.samchendev.weathertest.domain.models.WeatherInfo
import com.samchendev.weathertest.data.remote.WeatherApi

class WeatherRepoImpl(
    private val weatherApi: WeatherApi
) : WeatherRepo {
    override suspend fun getWeatherInfo(city: String): WeatherInfo? {
        val result = weatherApi.getWeather(city)

        if (!result.isSuccessful) {
            throw Exception("${result.errorBody()?.string()}")
        }

        return result.body()?.toWeatherInfo()
    }

    override suspend fun getWeatherInfo(lat: Double, lon: Double): WeatherInfo? {
        val result = weatherApi.getWeather(lat, lon)

        if (!result.isSuccessful) {
            throw Exception("${result.errorBody()?.string()}")
        }

        return result.body()?.toWeatherInfo()
    }
}