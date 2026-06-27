package com.samchendev.weathertest.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.samchendev.weathertest.presentation.navigation.NavDisplay
import com.samchendev.weathertest.ui.theme.WeatherTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherTestTheme {
                NavDisplay()
            }
        }
    }
}