package com.samchendev.weathertest.models.networkModels

import kotlinx.serialization.Serializable

@Serializable
data class Wind(
    val speed: Double,
    val deg: Int
)