package com.samchendev.weathertest.presentation.features.weatherSearch

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.samchendev.weathertest.R
import com.samchendev.weathertest.domain.models.WeatherInfo
import com.samchendev.weathertest.domain.userCases.GetLastCityUseCase
import com.samchendev.weathertest.domain.userCases.GetWeatherInfoUseCase
import com.samchendev.weathertest.domain.userCases.SaveLastCityUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.UnknownHostException

class WeatherSearchViewModel(
    private val getWeatherInfoUseCase: GetWeatherInfoUseCase,
    private val getLastCityUseCase: GetLastCityUseCase,
    private val saveLastCityUseCase: SaveLastCityUseCase
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
        val city = getLastCityUseCase() ?: return@launch

        updateIsProcessing(true)

        getWeatherInfoUseCase(city)
            .onSuccess { weatherInfo ->
                updateWeatherInfo(weatherInfo)
            }
            .onFailure { e ->

            }

        updateIsProcessing(false)
    }

    private fun getWeatherByCity() = uiScope.launch {
        updateIsProcessing(true)

        val city = cityState.text.toString()

        getWeatherInfoUseCase(city)
            .onSuccess { weatherInfo ->
                updateWeatherInfo(weatherInfo)
                saveLastCityUseCase(city)
            }
            .onFailure { e ->
                val message = when (e) {
                    is UnknownHostException -> R.string.no_internet_connection
                    else -> R.string.city_not_found_message
                }

                _errorMessage.emit(message)
            }

        updateIsProcessing(false)
    }

    private fun getMyCityWeather(context: Context) = uiScope.launch {
        updateIsProcessing(true)

        val location = getCurrentLocation(context) ?: run {
            updateIsProcessing(false)
            _errorMessage.emit(R.string.failed_to_get_location)
            return@launch
        }

        getWeatherInfoUseCase(location.latitude, location.longitude)
            .onSuccess { weatherInfo ->
                updateWeatherInfo(weatherInfo)
            }
            .onFailure { e ->
                val message = when (e) {
                    is UnknownHostException -> R.string.no_internet_connection
                    else -> R.string.city_not_found_message
                }

                _errorMessage.emit(message)
            }

        updateIsProcessing(false)
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