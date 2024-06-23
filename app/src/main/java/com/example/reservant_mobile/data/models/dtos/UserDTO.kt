package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
class UserDTO (
    val userId: String? = null,
    val login: String? = null,
    val firstName: String,
    val lastName: String,
    val roles: List<String>? = null,
    val phoneNumber: String? = null,
    val employments: List<RestaurantEmployeeDTO>? = null,
    val token: String? = null
)