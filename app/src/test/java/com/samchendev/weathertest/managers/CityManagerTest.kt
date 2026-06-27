package com.samchendev.weathertest.managers

import com.samchendev.weathertest.domain.managers.CityManager
import com.samchendev.weathertest.domain.managers.CityStorage
import com.samchendev.weathertest.utils.Constants
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CityManagerTest {
    @Test
    fun `saveCity(city) saves city using city key`() = runBlocking {
        val cityStorage = FakeCityStorage()
        val cityManager = CityManager(cityStorage)

        cityManager.saveCity("Boston")

        assertEquals(Constants.CITY_KEY, cityStorage.savedKey)
        assertEquals("Boston", cityStorage.savedValue)
    }

    @Test
    fun `getCity returns saved city`() = runBlocking {
        val cityStorage = FakeCityStorage()
        val cityManager = CityManager(cityStorage)

        cityManager.saveCity("New York")

        assertEquals("New York", cityManager.getCity())
    }

    private class FakeCityStorage : CityStorage {
        var savedKey: String? = null
        var savedValue: String? = null

        override suspend fun saveCity(key: String, value: String) {
            savedKey = key
            savedValue = value
        }

        override fun getCity(key: String): String? {
            return if (key == savedKey) savedValue else null
        }
    }
}