package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantMenuDTO (
    val id: Int? = null,
    val name: String,
    val alternateName: String?,
    val restaurantId: Int? = null,
    val name: String,
    val alternateName: String? = null,
    val menuType: String,
    val dateFrom: String,
    val dateUntil: String? = null,
    val menuItems: List<RestaurantMenuItemDTO>? = null
)
