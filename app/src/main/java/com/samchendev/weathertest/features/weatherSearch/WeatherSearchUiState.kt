package com.samchendev.weathertest.features.weatherSearch

import android.content.Context
import com.samchendev.weathertest.models.domainModels.WeatherInfo

data class WeatherSearchUiState(
    val weatherInfo: WeatherInfo?,
    val onSearchClick: () -> Unit,
    val onGetMyCityWeatherTrigger: (Context) -> Unit,
    val isProcessing: Boolean
)