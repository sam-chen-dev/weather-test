package com.samchendev.weathertest.presentation.features.weatherSearch

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.utlikotlin.Button
import com.example.utlikotlin.CoilImage
import com.example.utlikotlin.IconButton
import com.example.utlikotlin.Text
import com.example.utlikotlin.extensions.permissions.isGpsEnabled
import com.example.utlikotlin.extensions.permissions.isLocationPermissionGranted
import com.example.utlikotlin.extensions.permissions.launchEnableGps
import com.example.utlikotlin.extensions.permissions.launchLocationPermission
import com.example.utlikotlin.showToast
import com.samchendev.weathertest.R
import com.samchendev.weathertest.domain.models.WeatherInfo
import com.samchendev.weathertest.ui.theme.ExtraLarge
import com.samchendev.weathertest.utils.ProcessingDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun WeatherSearchScreen() {
    val viewModel: WeatherSearchViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cityState = viewModel.cityState
    val context = LocalContext.current
    val enableGpsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            uiState.onGetMyCityWeatherTrigger(context)
        } else {
            showToast(context, R.string.gps_not_enabled_message)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (context.isLocationPermissionGranted()) {
            if (!context.isGpsEnabled()) {
                enableGpsLauncher.launchEnableGps(context)
            } else {
                uiState.onGetMyCityWeatherTrigger(context)
            }
        } else {
            showToast(context, R.string.location_permission_not_granted_message)
        }
    }

    val onGetMyCityLocationClick: () -> Unit = { locationPermissionLauncher.launchLocationPermission() }

    LaunchedEffect(Unit) {
        viewModel.errorMessage.collect { resId ->
            showToast(context, resId)
        }
    }

    WeatherSearchContent(uiState, cityState, onGetMyCityLocationClick)
}

@Composable
private fun WeatherSearchContent(
    uiState: WeatherSearchUiState,
    cityState: TextFieldState,
    onGetMyCityLocationClick: () -> Unit
) {
    if (uiState.isProcessing) {
        ProcessingDialog()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Toolbar()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ControlArea(cityState, uiState.onSearchClick, onGetMyCityLocationClick)

            HorizontalDivider()

            uiState.weatherInfo?.let { weatherInfo ->
                InfoArea(weatherInfo)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar() {
    TopAppBar(
        title = { Text(R.string.weather_search_title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun ControlArea(
    cityState: TextFieldState,
    onSearchClick: () -> Unit,
    onGetMyCityLocationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CityTextField(cityState)
            Spacer(Modifier.width(8.dp))
            IconButton(R.drawable.ic_search, "Search", onSearchClick)
        }

        Spacer(Modifier.height(16.dp))

        Button("Get My City Weather", onGetMyCityLocationClick, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun RowScope.CityTextField(state: TextFieldState) {
    OutlinedTextField(
        state = state,
        label = { Text(R.string.city_label) },
        labelPosition = TextFieldLabelPosition.Attached(true),
        modifier = Modifier.weight(1F)
    )
}

@Composable
private fun InfoArea(weatherInfo: WeatherInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CoilImage(
                url = weatherInfo.iconUrl,
                contentDescription = "Weather icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(150.dp)
            )

            Text(weatherInfo.description, fontSize = ExtraLarge)
        }

        Spacer(Modifier.height(24.dp))

        Text(stringResource(R.string.location_text, weatherInfo.location))
        Text(stringResource(R.string.temperature_text, weatherInfo.temperature))
        Text(stringResource(R.string.feels_like_text, weatherInfo.feelsLike))
        Text(stringResource(R.string.min_temperature_text, weatherInfo.minTemperature))
        Text(stringResource(R.string.max_temperature_text, weatherInfo.maxTemperature))
        Text(stringResource(R.string.pressure_text, weatherInfo.pressure))
        Text(stringResource(R.string.humidity_text, weatherInfo.humidity))
        Text(stringResource(R.string.visibility_text, weatherInfo.visibility))
        Text(stringResource(R.string.wind_speed_text, weatherInfo.windSpeed))
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherSearchContentPreview() {
    WeatherSearchContent(
        uiState = WeatherSearchUiState(
            weatherInfo = WeatherInfo(
                description = "broken clouds",
                iconUrl = "https://openweathermap.org/payload/api/media/file/01d.png",
                temperature = 28.33,
                feelsLike = 28.54,
                minTemperature = 27.10,
                maxTemperature = 29.25,
                pressure = 1016,
                humidity = 47,
                visibility = 10000,
                windSpeed = 0.89,
                location = "Boston"
            ),
            isProcessing = false,
            onSearchClick = {},
            onGetMyCityWeatherTrigger = {}
        ),
        cityState = TextFieldState(),
        onGetMyCityLocationClick = {}
    )
}