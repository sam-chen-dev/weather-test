package com.samchendev.weathertest.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.samchendev.weathertest.utils.Constants
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object OpenWeatherService {
    private val contentType = "application/json".toMediaType()
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val interceptor = Interceptor { chain ->
        val newUrl = chain.request().url.newBuilder().run {
            addQueryParameter(Constants.OPEN_WEATHER_API_UNIT_NAME, Constants.OPEN_WEATHER_API_UNIT_VALUE)
            addQueryParameter(Constants.OPEN_WEATHER_API_KEY_NAME, Constants.OPEN_WEATHER_API_KEY_VALUE)
            build()
        }

        val newRequest = chain.request().newBuilder().run {
            url(newUrl)
            build()
        }

        chain.proceed(newRequest)
    }

    private val okHttpClient = OkHttpClient.Builder().run {
        addInterceptor(interceptor)
        build()
    }

    private val retrofit = Retrofit.Builder().run {
        addConverterFactory(json.asConverterFactory(contentType))
        client(okHttpClient)
        baseUrl(Constants.OPEN_WEATHER_BASE_URL)
        build()
    }

    val weatherApi: WeatherApi by lazy {
        retrofit.create(WeatherApi::class.java)
    }
}