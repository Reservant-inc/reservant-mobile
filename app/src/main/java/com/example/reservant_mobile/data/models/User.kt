package com.example.reservant_mobile.data.models

import kotlinx.serialization.*

@Serializable
data class User(
    val firstName: String,
    val lastName: String,
    val birthday: String,
    val email: String,
    val phoneNum: String,
)