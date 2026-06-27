package com.samchendev.weathertest.domain.userCases

import com.samchendev.weathertest.domain.managers.CityManager

class GetLastCityUseCase(private val cityManager: CityManager) {
    operator fun invoke(): String? {
        return cityManager.getCity()
    }
}