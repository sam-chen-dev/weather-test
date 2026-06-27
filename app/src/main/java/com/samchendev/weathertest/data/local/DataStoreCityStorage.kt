package com.samchendev.weathertest.data.local

import com.example.utlikotlin.DataStore
import com.samchendev.weathertest.domain.managers.CityStorage

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