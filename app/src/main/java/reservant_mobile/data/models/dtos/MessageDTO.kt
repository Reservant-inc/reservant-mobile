package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable
@Serializable
data class MessageDTO(
    val text: String,
    val isSentByMe: Boolean
)