package reservant_mobile.data.models.dtos

import com.example.reservant_mobile.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

@Serializable()
data class NotificationDTO(
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
    val photo: String? = null,
    val details: Map<String, JsonElement>? = null
){
    @Serializable
    enum class NotificationType(
        val titleResourceId: Int,
        val bodyResourceId: Int
    ) {
        NotificationRestaurantVerified(
            R.string.label_NotificationRestaurantVerified,
            R.string.content_NotificationRestaurantVerified
        ){
            override fun getTitleArguments(details: Map<String, JsonElement>?): Array<Any> = arrayOf()

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

        },
        NotificationNewRestaurantReview(
            R.string.label_NotificationNewRestaurantReview,
            R.string.content_NotificationNewRestaurantReview
        ){
            override fun getTitleArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

        },
        NotificationNewFriendRequest(
            R.string.label_NotificationNewFriendRequest,
            0
        ){
            override fun getTitleArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

        },
        NotificationFriendRequestAccepted(
            R.string.label_NotificationFriendRequestAccepted,
            0
        ){
            override fun getTitleArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

        },
        NotificationNewParticipationRequest(
            R.string.label_NotificationNewParticipationRequest,
            0
        ){
            override fun getTitleArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

        },
        NotificationParticipationRequestResponse(
            R.string.label_NotificationParticipationRequestResponse,
            0
        ){
            override fun getTitleArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

        },
        NotificationVisitApprovedDeclined(
            R.string.label_NotificationVisitApprovedDeclined,
            0
        ){
            override fun getTitleArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

        },
        NotificationNewMessage(
            R.string.label_NotificationNewMessage,
            0
        ){
            override fun getTitleArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

        },
        NotificationNewReservation(
            R.string.label_NotificationNewReservation,
            0
        ){
            override fun getTitleArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

        };

        abstract fun getTitleArguments(details: Map<String, JsonElement>?): Array<Any>
        abstract fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any>
    }
}

@Serializable
sealed interface NotificationDetails

@Serializable
data class RestaurantVerifiedDetails(
    val restaurantId: Int,
    val restaurantName: String
) : NotificationDetails

@Serializable
data class NewRestaurantReviewDetails(
    val restaurantId: Int,
    val restaurantName: String,
    val reviewId: Int,
    val stars: Int,
    val contents: String,
    val authorId: String,
    val authorName: String
) : NotificationDetails

@Serializable
data class NewFriendRequestDetails(
    val friendRequestId: Int,
    val senderId: String,
    val senderName: String
) : NotificationDetails

@Serializable
data class FriendRequestAcceptedDetails(
    val friendRequestId: Int,
    val acceptingUserId: String,
    val acceptingUserFullName: String
) : NotificationDetails

@Serializable
data class NewParticipationRequestDetails(
    val senderId: String,
    val senderName: String,
    val eventId: Int,
    val eventName: String
) : NotificationDetails

@Serializable
data class ParticipationRequestResponseDetails(
    val eventId: Int,
    val name: String,
    val creatorId: String,
    val creatorName: String,
    val isAccepted: Boolean
) : NotificationDetails

@Serializable
data class VisitApprovedDeclinedDetails(
    val visitId: Int,
    val isAccepted: Boolean,
    val restaurantName: String,
    /***
     * Date as String in 'yyyy-MM-d'T'H:mm:ss.SSS'Z'' format
     */
    val date: String
) : NotificationDetails

@Serializable
data class NotificationNewMessage(
    val messageId : Int,
    val threadId : Int,
    val threadTitle : String,
    val authorId : String,
    val authorName : String,
    val contents : String
)

@Serializable
data class NotificationNewReservation(
    val restaurantId : Int,
    val restaurantName : String,
    val date : String,
    val endTime : String,
    val numberOfPeople : Int,
    val takeaway : Boolean
)


inline fun <reified T> Map<String, Any>.toObject(): T {
    val jsonElement = JsonObject(this.mapValues { Json.encodeToJsonElement(it.value) })
    return Json.decodeFromJsonElement(jsonElement)
}
