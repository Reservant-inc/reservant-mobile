package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class LocationDTO (
    val latitude: Double,
    val longitude: Double

)