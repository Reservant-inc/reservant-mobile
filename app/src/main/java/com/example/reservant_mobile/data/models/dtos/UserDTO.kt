package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
class UserDTO (
    val id: String = "",
    val login: String,
    val firstName: String,
    val lastName: String,
    val roles: List<String> = emptyList(),
    val phoneNumber: String = "",
    val employments: List<RestaurantEmployeeDTO> = emptyList(),
    val token: String = ""
)