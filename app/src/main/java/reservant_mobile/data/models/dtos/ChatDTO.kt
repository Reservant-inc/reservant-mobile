package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ChatDTO(
    val userName: String,
    val lastMessage: String,
    val timeStamp: String
)