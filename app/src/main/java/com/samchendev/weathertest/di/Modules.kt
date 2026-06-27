package com.samchendev.weathertest.di

import com.example.utlikotlin.DataStore
import com.samchendev.weathertest.domain.managers.CityStorage
import com.samchendev.weathertest.data.local.DataStoreCityStorage
import com.samchendev.weathertest.data.remote.OpenWeatherService
import com.samchendev.weathertest.data.repos.WeatherRepoImpl
import com.samchendev.weathertest.domain.managers.CityManager
import com.samchendev.weathertest.domain.repos.WeatherRepo
import com.samchendev.weathertest.domain.userCases.GetLastCityUseCase
import com.samchendev.weathertest.domain.userCases.GetWeatherInfoUseCase
import com.samchendev.weathertest.domain.userCases.SaveLastCityUseCase
import com.samchendev.weathertest.presentation.features.weatherSearch.WeatherSearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    /*Managers*/
    single { DataStore(androidContext()) }
    single<CityStorage> { DataStoreCityStorage(get()) }
    single { CityManager(get()) }

    /*Services*/
    single { OpenWeatherService.weatherApi }

    /*Database*/

    /*Repos*/
    single<WeatherRepo> { WeatherRepoImpl(get()) }

    /*UseCase*/
    single { GetWeatherInfoUseCase(get()) }
    single { GetLastCityUseCase(get()) }
    single { SaveLastCityUseCase(get()) }

    /*ViewModels*/
    viewModel { WeatherSearchViewModel(get(), get(), get()) }
}