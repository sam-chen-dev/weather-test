package com.samchendev.weathertest.managers

import com.example.utlikotlin.DataStore
import com.samchendev.weathertest.utils.Constants

class CityManager(private val dataStore: DataStore) {
    suspend fun saveCity(city: String) {
        dataStore.saveString(Constants.CITY_KEY, city)
    }

    fun getCity(): String? {
        return dataStore.getString(Constants.CITY_KEY)
    }
}