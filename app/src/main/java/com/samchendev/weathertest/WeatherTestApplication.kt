package com.samchendev.weathertest

import android.app.Application
import com.samchendev.weathertest.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin()
    }

    private fun initKoin() = startKoin {
        androidContext(this@WeatherApplication)
        modules(listOf(appModule))
    }
}