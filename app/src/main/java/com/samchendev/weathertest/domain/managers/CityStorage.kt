package com.samchendev.weathertest.domain.managers

interface CityStorage {
    suspend fun saveCity(key: String, value: String)

    fun getCity(key: String): String?
}