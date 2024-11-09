package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TableDTO (
    val tableId: Int,
    val capacity: Int,
    val visitId: Int? = null
)