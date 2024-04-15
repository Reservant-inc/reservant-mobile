package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class FileUploadDTO(
    val path: String,
    val fileName: String,
    val contentType: String,
)