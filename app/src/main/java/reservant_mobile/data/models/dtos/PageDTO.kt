package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PageDTO <out T : Any>(
    val page: Int,
    val totalPages: Int,
    val perPage: Int,
    val orderByOptions: List<String>?= null,
    val items: List<T>
)