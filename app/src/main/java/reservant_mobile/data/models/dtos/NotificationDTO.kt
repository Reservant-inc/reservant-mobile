package reservant_mobile.data.models.dtos

import com.example.reservant_mobile.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

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
            R.string.title_NotificationRestaurantVerified,
            R.string.body_NotificationRestaurantVerified
        ){
            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                return arrayOf(details?.get("restaurantName")?.jsonPrimitive?.content ?: "")
            }

        },
        NotificationNewRestaurantReview(
            R.string.title_NotificationNewRestaurantReview,
            R.string.body_NotificationNewRestaurantReview
        ){
            override fun getTitleArguments(details: Map<String, JsonElement>?): Array<Any> {
                return arrayOf(details?.get("restaurantName")?.jsonPrimitive?.content ?: "")
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                val starCount = details?.get("stars")?.jsonPrimitive?.intOrNull ?: 0

                val stars = "★".repeat(starCount) + "☆".repeat(5 - starCount)

                return arrayOf(
                    stars,
                    details?.get("authorName")?.jsonPrimitive?.content ?: "",
                    details?.get("contents")?.jsonPrimitive?.content ?: ""
                )
            }

        },
        NotificationNewFriendRequest(
            R.string.title_NotificationNewFriendRequest,
            R.string.body_NotificationNewFriendRequest
        ){
            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                return arrayOf(
                    details?.get("senderName")?.jsonPrimitive?.content ?: ""
                )
            }

        },
        NotificationFriendRequestAccepted(
            R.string.title_NotificationFriendRequestAccepted,
            R.string.body_NotificationFriendRequestAccepted
        ){
            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                return arrayOf(
                    details?.get("acceptingUserFullName")?.jsonPrimitive?.content ?: ""
                )
            }

        },
        NotificationNewParticipationRequest(
            R.string.title_NotificationNewParticipationRequest,
            R.string.body_NotificationNewParticipationRequest
        ){
            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                return arrayOf(
                    details?.get("senderName")?.jsonPrimitive?.content ?: "",
                    details?.get("eventName")?.jsonPrimitive?.content ?: "",
                )
            }

        },
        NotificationParticipationRequestResponse(
            R.string.title_NotificationParticipationRequestResponse,
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
            R.string.title_NotificationNewMessage,
            R.string.body_NotificationNewMessage
        ){
            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                return arrayOf(
                    details?.get("authorName")?.jsonPrimitive?.content ?: "",
                    details?.get("contents")?.jsonPrimitive?.content ?: "",
                )
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

        open fun getTitleArguments(details: Map<String, JsonElement>?): Array<Any> = arrayOf()
        open fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> = arrayOf()
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
