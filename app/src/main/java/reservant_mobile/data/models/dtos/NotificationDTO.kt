package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDTO (
    val notificationId: Int? = null,
    /***
     * Date in 'yyyy-MM-d'T'H:mm:ss.SSS'Z'' format
     */
    val dateCreated: String? = null,
    /***
     * Date in 'yyyy-MM-d'T'H:mm:ss.SSS'Z'' format
     */
    val dateRead: String? = null,
    val notificationType: NotificationType? = null,
    val details: String? = null
){
    @Serializable
    enum class NotificationType{
        UserInterestedInYourEvent,
        AcceptedToEvent,
        RejectedFromEvent,
        RestaurantVerified,
        NewRestaurantReview
    }
}