package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
class LoginResponseDTO (
    val token: String,
    val login: String,
    val firstName: String,
    val lastName: String,
    val roles: List<String>
)