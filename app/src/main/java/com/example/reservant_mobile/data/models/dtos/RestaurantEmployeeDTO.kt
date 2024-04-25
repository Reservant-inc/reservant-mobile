package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantEmployeeDTO (
    val id: String? = null,
    val login: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val password: String? = null,
    val roles: List<String>? = null,
    val isHallEmployee:Boolean? = null,
    val isBackdoorEmployee:Boolean? = null,
    val restaurantId: String = null,
    val dateFrom: String = null,
    val dateUntil: String? = null,
    val employmentId: String? = null
)