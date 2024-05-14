package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantMenuItemDTO (
    val id: Int? = null,
    val restaurantId: Int? = null,
    val price: Double,
    val name: String,
    val alternateName: String? = null,
    val alcoholPercentage: Double?
)