package com.samchendev.weathertest.models.networkModels

import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)