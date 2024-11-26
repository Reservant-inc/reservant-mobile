package reservant_mobile.data.models.dtos

import com.example.reservant_mobile.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import reservant_mobile.data.utils.formatToDateTime

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
    enum class NotificationType {
        NotificationRestaurantVerified{
            override fun getTitleResource(details: Map<String, JsonElement>?): Int {
                return R.string.title_NotificationRestaurantVerified
            }

            override fun getBodyResource(details: Map<String, JsonElement>?): Int {
                return R.string.body_NotificationRestaurantVerified
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                return arrayOf(details?.get("restaurantName")?.jsonPrimitive?.content ?: "")
            }

        },
        NotificationNewRestaurantReview{
            override fun getTitleResource(details: Map<String, JsonElement>?): Int {
                return R.string.title_NotificationNewRestaurantReview
            }

            override fun getBodyResource(details: Map<String, JsonElement>?): Int {
                return R.string.body_NotificationNewRestaurantReview
            }

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
        NotificationNewFriendRequest{
            override fun getTitleResource(details: Map<String, JsonElement>?): Int {
                return R.string.title_NotificationNewFriendRequest
            }

            override fun getBodyResource(details: Map<String, JsonElement>?): Int {
                return R.string.body_NotificationNewFriendRequest
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                return arrayOf(
                    details?.get("senderName")?.jsonPrimitive?.content ?: ""
                )
            }

        },
        NotificationFriendRequestAccepted{
            override fun getTitleResource(details: Map<String, JsonElement>?): Int {
                return R.string.title_NotificationFriendRequestAccepted
            }

            override fun getBodyResource(details: Map<String, JsonElement>?): Int {
                return R.string.body_NotificationFriendRequestAccepted
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                return arrayOf(
                    details?.get("acceptingUserFullName")?.jsonPrimitive?.content ?: ""
                )
            }

        },
        NotificationNewParticipationRequest{
            override fun getTitleResource(details: Map<String, JsonElement>?): Int {
                return R.string.title_NotificationNewParticipationRequest
            }

            override fun getBodyResource(details: Map<String, JsonElement>?): Int {
                return R.string.body_NotificationNewParticipationRequest
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                return arrayOf(
                    details?.get("senderName")?.jsonPrimitive?.content ?: "",
                    details?.get("eventName")?.jsonPrimitive?.content ?: "",
                )
            }

        },
        NotificationParticipationRequestResponse{
            override fun getTitleResource(details: Map<String, JsonElement>?): Int {
                val isAccepted =  details?.get("isAccepted")?.jsonPrimitive?.booleanOrNull == true

                return if (isAccepted)
                    R.string.title_NotificationParticipationRequestResponse_accepted
                else R.string.title_NotificationParticipationRequestResponse_declined
            }

            override fun getBodyResource(details: Map<String, JsonElement>?): Int {
                val isAccepted =  details?.get("isAccepted")?.jsonPrimitive?.booleanOrNull == true

                return if (isAccepted)
                    R.string.body_NotificationParticipationRequestResponse_accepted
                else R.string.body_NotificationParticipationRequestResponse_declined
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                return arrayOf(
                    details?.get("creatorName")?.jsonPrimitive?.content ?: "",
                    details?.get("name")?.jsonPrimitive?.content ?: ""
                )
            }

        },
        NotificationVisitApprovedDeclined{
            override fun getTitleResource(details: Map<String, JsonElement>?): Int {
                return R.string.label_NotificationVisitApprovedDeclined
            }

            override fun getBodyResource(details: Map<String, JsonElement>?): Int {
                TODO("Not yet implemented")
            }

            override fun getTitleArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                TODO("Not yet implemented")
            }

        },
        NotificationNewMessage{
            override fun getTitleResource(details: Map<String, JsonElement>?): Int {
                return R.string.title_NotificationNewMessage
            }

            override fun getBodyResource(details: Map<String, JsonElement>?): Int {
                return R.string.body_NotificationNewMessage
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                return arrayOf(
                    details?.get("authorName")?.jsonPrimitive?.content ?: "",
                    details?.get("contents")?.jsonPrimitive?.content ?: "",
                )
            }

        },
        NotificationNewReservation{
            override fun getTitleResource(details: Map<String, JsonElement>?): Int {
                return R.string.title_NotificationNewReservation
            }

            override fun getBodyResource(details: Map<String, JsonElement>?): Int {
                return R.string.body_NotificationNewReservation
            }

            override fun getBodyArguments(details: Map<String, JsonElement>?): Array<Any> {
                val date = formatToDateTime(
                    details?.get("numberOfPeople")?.jsonPrimitive?.content ?: "",
                    "dd-MM-yyyy"
                )

                val numOfPeople = details?.get("numberOfPeople")?.jsonPrimitive?.intOrNull ?: 0

                return arrayOf(
                    details?.get("restaurantName")?.jsonPrimitive?.content ?: "",
                    date,
                    numOfPeople,
                )
            }

        };

        abstract fun getTitleResource(details: Map<String, JsonElement>?): Int
        abstract fun getBodyResource(details: Map<String, JsonElement>?): Int

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
