package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TableDTO (
    val id: Int,
    val capacity: Int
)