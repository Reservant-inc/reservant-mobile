package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantMenuDTO (
    val id: Int? = null,
    val restaurantId: Int? = null,
    val menuType: String = null,
    val dateFrom: String = null,
    val dateUntil: String? = null,
    val menuItems: List<RestaurantMenuItemDTO>? = null
)
