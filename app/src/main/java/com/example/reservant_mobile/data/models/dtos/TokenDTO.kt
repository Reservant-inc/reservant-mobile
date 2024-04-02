package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TokenDTO(
    val token: String,
//    val refreshToken: String,
//    val expirationTimeInMillis: Long
)