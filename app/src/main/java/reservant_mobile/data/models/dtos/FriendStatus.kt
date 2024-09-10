package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
enum class FriendStatus {
    Stranger,
    OutgoingRequest,
    IncomingRequest,
    Friend
}