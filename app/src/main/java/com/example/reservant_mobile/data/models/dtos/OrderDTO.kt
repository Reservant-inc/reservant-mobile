package com.example.reservant_mobile.data.models.dtos
import kotlinx.serialization.Serializable

@Serializable
data class OrderDTO(
    val date: String,
    val cost: String,
    val customer: String,
    val status: String
)