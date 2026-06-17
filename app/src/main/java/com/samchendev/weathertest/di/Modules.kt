package com.samchendev.weathertest.di

import com.example.utlikotlin.DataStore
import com.samchendev.weathertest.features.weatherSearch.WeatherSearchViewModel
import com.samchendev.weathertest.managers.CityManager
import com.samchendev.weathertest.repos.WeatherRepoImpl
import com.samchendev.weathertest.services.OpenWeatherService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    /*Managers*/
    single { DataStore(androidContext()) }
    single { CityManager(get()) }

    /*Services*/
    single { OpenWeatherService.weatherApi }

    /*Database*/

    /*Repos*/
    single { WeatherRepoImpl(get()) }

    /*ViewModels*/
    viewModel { WeatherSearchViewModel(get(), get()) }
}