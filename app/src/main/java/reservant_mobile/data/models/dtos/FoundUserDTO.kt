package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class FoundUserDTO (
    val userId: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val photo: String? = null,
    val friendStatus: FriendStatus? = null
)