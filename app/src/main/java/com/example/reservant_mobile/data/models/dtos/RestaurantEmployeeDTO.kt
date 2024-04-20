package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantEmployeeDTO (
    val id: Int = 0,
    val login: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val roles: List<String> = emptyList(),
    val isHallEmployee:Boolean = false,
    val isBackdoorEmployee:Boolean = false
)