package com.samchendev.weathertest.managers.cityManager

import com.samchendev.weathertest.utils.Constants

class CityManager(private val cityStorage: CityStorage) {
    suspend fun saveCity(city: String) {
        cityStorage.saveCity(Constants.CITY_KEY, city)
    }

    fun getCity(): String? {
        return cityStorage.getCity(Constants.CITY_KEY)
    }
}