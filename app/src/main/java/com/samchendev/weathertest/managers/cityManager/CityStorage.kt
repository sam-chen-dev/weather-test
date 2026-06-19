package com.samchendev.weathertest.managers.cityManager

interface CityStorage {
    suspend fun saveCity(key: String, value: String)

    fun getCity(key: String): String?
}