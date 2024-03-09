package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.*

@Serializable
data class RegisterUserDTO(
    val firstName: String,
    val lastName: String,
    val birthday: String,
    val email: String,
    val phoneNum: String,
) 