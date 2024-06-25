package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PageDTO <out T : Any>(
    val page: Int,
    val totalPages: Int,
    val perPage: Int,
    val items: List<T>
)