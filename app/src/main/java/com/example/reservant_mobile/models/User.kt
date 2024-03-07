package com.example.reservant_mobile.models

import kotlinx.serialization.*

@Serializable
data class User(
    val firstName: String,
    val lastName: String,
    val birthday: String,
    val email: String,
    val phoneNum: String,
)