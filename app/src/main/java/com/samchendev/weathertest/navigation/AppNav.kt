package com.samchendev.weathertest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.samchendev.weathertest.features.weatherSearch.WeatherSearchScreen
import kotlinx.serialization.Serializable

@Serializable
data object WeatherSearch : NavKey

@Composable
fun EntryProviderScope<NavKey>.WeatherSearchEntry(backStack: NavBackStack<NavKey>) {
    entry<WeatherSearch> {
        WeatherSearchScreen()
    }
}