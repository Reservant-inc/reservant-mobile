package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginDTO(
    val login: String,
    val password: String
)