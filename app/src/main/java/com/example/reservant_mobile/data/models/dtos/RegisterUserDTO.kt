package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.*

@Serializable
data class RegisterUserDTO(
    val login : String,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val email: String,
    val phoneNumber: String = "",
    val password: String
) 