package reservant_mobile.data.models.dtos

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import reservant_mobile.data.models.dtos.NotificationDTO.NotificationType

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
    val details: Map<String, String>? = null
){
    @Serializable
    enum class NotificationType{
        NotificationRestaurantVerified,
        NotificationNewRestaurantReview,
        NotificationNewFriendRequest,
        NotificationFriendRequestAccepted,
        NotificationNewParticipationRequest,
        NotificationParticipationRequestResponse,
        NotificationVisitApprovedDeclined
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


inline fun <reified T> Map<String, Any>.toObject(): T {
    val jsonElement = JsonObject(this.mapValues { Json.encodeToJsonElement(it.value) })
    return Json.decodeFromJsonElement(jsonElement)
}
