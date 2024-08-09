package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TicketDTO(
    val title: String,
    val report: String,
    val category: String,
    val date: String
    )