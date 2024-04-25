package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantGroupDTO (
    val id: Int? = null,
    val name: String,
    val restaurants: List<RestaurantDTO>? = null,
    val restaurantCount: Int? = restaurants?.size,
    val restaurantIds: List<Int>? = null
)