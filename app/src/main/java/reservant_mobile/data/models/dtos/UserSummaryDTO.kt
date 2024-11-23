package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable
import reservant_mobile.data.endpoints.Employments

@Serializable
data class UserSummaryDTO (
    val userId: String? = null,
    val login: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val birthDate: String? = null,
    val phoneNumber: PhoneNumberDTO? = null,
    val employments: List<Employments>? = null,
    val photo: String? = null,
    val friendStatus: FriendStatus? = null
) {
    @Serializable
    enum class FriendStatus{
        Stranger,
        OutgoingRequest,
        IncomingRequest,
        Friend
    }
}