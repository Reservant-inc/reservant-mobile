package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PointDTO(
    val latitude: Double,
    val longitude: Double,
)