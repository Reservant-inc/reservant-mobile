package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class RegisterRestaurantDTO (
    val name: String,
    val nip: String,
    val restaurantType: String,
    val address: String,
    val postalCode: String,
    val city: String,
    val lease: String,
    val license: String,
    val consent: String,
    val idCard: String
)