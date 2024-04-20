package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantGroupDTO (
    val id: Int,
    val name: String,
    val restaurantCount: Int = 0,
    val restaurants: List<RestaurantDTO> = emptyList()
)