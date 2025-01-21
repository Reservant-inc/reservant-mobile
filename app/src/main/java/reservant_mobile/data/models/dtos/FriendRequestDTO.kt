package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestDTO(
    /***
     * Date in 'yyyy-MM-dd'T'HH:mm:ss.SSS'Z'' format
     */
    val dateSent: String,
    /***
     * Date in 'yyyy-MM-dd'T'HH:mm:ss.SSS'Z'' format
     */
    val dateRead: String?,
    /***
     * Date in 'yyyy-MM-dd'T'HH:mm:ss.SSS'Z'' format
     */
    val dateAccepted: String?,
    val otherUser: OtherUserDTO? = null,
    val privateMessageThreadId: Int? = null
){
    @Serializable
    data class OtherUserDTO(
        val userId: String,
        val firstName: String? = null,
        val lastName: String? = null,
        val photo: String? = null
    )
}