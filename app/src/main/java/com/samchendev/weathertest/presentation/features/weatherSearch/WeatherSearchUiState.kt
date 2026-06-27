package com.samchendev.weathertest.presentation.features.weatherSearch

import android.content.Context
import com.samchendev.weathertest.domain.models.WeatherInfo

data class WeatherSearchUiState(
    val weatherInfo: WeatherInfo?,
    val isProcessing: Boolean,
    val onSearchClick: () -> Unit,
    val onGetMyCityWeatherTrigger: (Context) -> Unit
)