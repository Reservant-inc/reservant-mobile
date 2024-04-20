package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantGroupDTO (
    val id: Int = 0,
    val name: String,
    val restaurants: List<RestaurantDTO> = emptyList(),
    val restaurantCount: Int = restaurants.size,
    val restaurantIds: List<Int> = emptyList()
)