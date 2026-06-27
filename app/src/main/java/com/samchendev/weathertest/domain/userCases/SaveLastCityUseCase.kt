package com.samchendev.weathertest.domain.userCases

import com.samchendev.weathertest.domain.managers.CityManager

class SaveLastCityUseCase(private val cityManager: CityManager) {
    suspend operator fun invoke(city: String) {
        cityManager.saveCity(city)
    }
}