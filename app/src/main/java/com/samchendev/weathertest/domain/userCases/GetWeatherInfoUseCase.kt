package com.samchendev.weathertest.domain.userCases

import com.samchendev.weathertest.domain.models.WeatherInfo
import com.samchendev.weathertest.domain.repos.WeatherRepo

class GetWeatherInfoUseCase(private val weatherRepo: WeatherRepo) {
    suspend operator fun invoke(city: String): Result<WeatherInfo> {
        return weatherRepo.getWeatherInfo(city)
    }

    suspend operator fun invoke(lat: Double, lon: Double): Result<WeatherInfo> {
        return weatherRepo.getWeatherInfo(lat, lon)
    }
}