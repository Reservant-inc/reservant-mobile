package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
class UserDTO (
    val id: String? = null,
    val login: String,
    val firstName: String,
    val lastName: String,
    val roles: List<String>? = null,
    val phoneNumber: String? = null,
    val employments: List<RestaurantEmployeeDTO>? = null,
    val token: String? = null
)