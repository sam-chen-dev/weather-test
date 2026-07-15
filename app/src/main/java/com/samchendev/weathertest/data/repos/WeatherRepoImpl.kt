package com.samchendev.weathertest.data.repos

import android.util.Log
import com.samchendev.weathertest.data.remote.WeatherApi
import com.samchendev.weathertest.domain.models.WeatherInfo
import com.samchendev.weathertest.domain.repos.WeatherRepo

class WeatherRepoImpl(
    private val weatherApi: WeatherApi
) : WeatherRepo {
    companion object {
        private const val TAG = "WeatherRepoImpl"
    }

    override suspend fun getWeatherInfo(city: String): Result<WeatherInfo> {
        return try {
            val weatherInfo = weatherApi.getWeather(city).toWeatherInfo()

            Result.success(weatherInfo)
        } catch (e: Exception) {
            Log.e(TAG, "getWeatherInfo(city: String) failed", e)
            Result.failure(e)
        }
    }

    override suspend fun getWeatherInfo(lat: Double, lon: Double): Result<WeatherInfo> {
        return try {
            val weatherInfo = weatherApi.getWeather(lat, lon).toWeatherInfo()

            Result.success(weatherInfo)
        } catch (e: Exception) {
            Log.e(TAG, "getWeatherInfo(lat: Double, lon: Double) failed", e)
            Result.failure(e)
        }
    }
}