package com.samchendev.weathertest.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class Wind(
    val speed: Double,
    val deg: Int
)