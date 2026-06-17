package com.samchendev.weathertest.features.weatherSearch

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.samchendev.weathertest.R
import com.samchendev.weathertest.managers.CityManager
import com.samchendev.weathertest.models.domainModels.WeatherInfo
import com.samchendev.weathertest.repos.WeatherRepoImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class WeatherSearchViewModel(
    private val weatherRepo: WeatherRepoImpl,
    private val cityManager: CityManager
) : ViewModel() {
    private val uiScope = viewModelScope
    private val _uiState = MutableStateFlow(createUiState())
    private val _errorMessage = MutableSharedFlow<Int>()

    val cityState = TextFieldState()

    val uiState = _uiState.asStateFlow()
    val errorMessage = _errorMessage.asSharedFlow()

    init {
        getWeatherForLastCity()
    }

    private fun createUiState(): WeatherSearchUiState = WeatherSearchUiState(
        weatherInfo = null,
        isProcessing = false,
        onSearchClick = ::getWeatherByCity,
        onGetMyCityWeatherTrigger = ::getMyCityWeather,
    )

    private fun getWeatherForLastCity() = uiScope.launch {
        try {
            val city = cityManager.getCity() ?: return@launch

            updateIsProcessing(true)

            val weatherInfo = weatherRepo.getWeatherInfo(city)

            weatherInfo?.let { updateWeatherInfo(it) }

            updateIsProcessing(false)
        } catch (e: Exception) {
            Log.e("getWeatherForLastCity()", "Error: ${e.message.toString()}")
            updateIsProcessing(false)
        }
    }

    private fun getWeatherByCity() = uiScope.launch {
        try {
            updateIsProcessing(true)

            val city = cityState.text.toString()
            val weatherInfo = weatherRepo.getWeatherInfo(city)

            weatherInfo?.let {
                updateWeatherInfo(it)
                cityManager.saveCity(city)
            }

            updateIsProcessing(false)
        } catch (e: Exception) {
            Log.e("getWeatherByCity()", "Error: ${e.message.toString()}")
            updateIsProcessing(false)
            _errorMessage.emit(R.string.city_not_found_message)
        }
    }

    private fun getMyCityWeather(context: Context) = uiScope.launch {
        try {
            updateIsProcessing(true)

            val location = getCurrentLocation(context) ?: throw Exception("Location is null")
            val weatherInfo = weatherRepo.getWeatherInfo(location.latitude, location.longitude)

            weatherInfo?.let { updateWeatherInfo(it) }

            updateIsProcessing(false)
        } catch (e: Exception) {
            Log.e("getMyCityWeather()", "Error: ${e.message.toString()}")
            updateIsProcessing(false)
            _errorMessage.emit(R.string.unknown_error)
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? {
        val locationClient = LocationServices.getFusedLocationProviderClient(context)
        val location = suspendCancellableCoroutine { continuation ->
            locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener {
                    continuation.resumeWith(Result.success(it))
                }
                .addOnFailureListener {
                    continuation.resumeWith(Result.success(null))
                }
        }

        return location
    }

    private fun updateWeatherInfo(weatherInfo: WeatherInfo) = _uiState.update { it.copy(weatherInfo = weatherInfo) }
    private fun updateIsProcessing(isProcessing: Boolean) = _uiState.update { it.copy(isProcessing = isProcessing) }
}