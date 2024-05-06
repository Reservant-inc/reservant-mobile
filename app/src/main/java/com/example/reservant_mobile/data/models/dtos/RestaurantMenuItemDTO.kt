package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantMenuItemDTO (
    val id: Int = 0,
    val price: Double,
    val name: String,
    val alcoholPercentage: Double?
)