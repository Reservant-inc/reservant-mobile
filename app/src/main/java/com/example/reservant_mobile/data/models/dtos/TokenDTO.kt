package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TokenDTO(
    val bearerToken: String,
    val refreshToken: String,
    val expirationTimeInMillis: Long
)