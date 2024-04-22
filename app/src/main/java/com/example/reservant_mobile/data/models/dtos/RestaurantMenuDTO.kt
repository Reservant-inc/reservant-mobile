package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantMenuDTO (
    val id: Int = 0,
    val menuType: String,
    val dateFrom: String,
    val dateUntil: String?,
    val menuItems: List<RestaurantMenuItemDTO> = emptyList()
)
