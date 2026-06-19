package com.samchendev.weathertest.managers.cityManager

import com.example.utlikotlin.DataStore

class DataStoreCityStorage(
    private val dataStore: DataStore
) : CityStorage {
    override suspend fun saveCity(key: String, value: String) {
        dataStore.saveString(key, value)
    }

    override fun getCity(key: String): String? {
        return dataStore.getString(key)
    }
}