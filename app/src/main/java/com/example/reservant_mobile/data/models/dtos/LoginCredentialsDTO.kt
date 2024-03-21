package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class LoginCredentialsDTO(
    val login: String,
    val password: String,
    val rememberMe: Boolean
)