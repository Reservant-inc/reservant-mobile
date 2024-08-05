package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestDTO(
    /***
     * Date in 'yyyy-MM-d'T'H:mm:ss.yyyy'Z'' format
     */
    val dateSent: String,
    /***
     * Date in 'yyyy-MM-d'T'H:mm:ss.yyyy'Z'' format
     */
    val dateRead: String,
    /***
     * Date in 'yyyy-MM-d'T'H:mm:ss.yyyy'Z'' format
     */
    val dateAccepted: String,
    val senderId: String,
    val receiverId: String,
    val senderName: String,
    val receiverName: String
)