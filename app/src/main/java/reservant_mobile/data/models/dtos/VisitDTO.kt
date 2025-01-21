package reservant_mobile.data.models.dtos

import androidx.datastore.preferences.protobuf.BoolValueOrBuilder
import kotlinx.serialization.Serializable

@Serializable
data class VisitDTO(
    val visitId: Int? = null,
    /***
     * Date in 'yyyy-MM-d'T'H:mm:ss.SSS'Z'' format
     */
    val date: String? = null,
    val endTime: String? = null,
    /***
     * Date in 'yyyy-MM-d'T'H:mm:ss.SSS'Z'' format
     */
    val actualStartTime: String? = null,
    /***
     * Date in 'yyyy-MM-d'T'H:mm:ss.SSS'Z'' format
     */
    val actualEndTime: String? = null,
    val numberOfGuests: Int? = null,
    val numberOfPeople: Int? = null,
    val paymentTime: String? = null,
    val deposit: Double? = null,
    /***
     * Date in 'yyyy-MM-dd' format
     */
    val reservationDate: String? = null,
    val tip: Double? = null,
    val takeaway: Boolean? = null,
    val clientId: String? = null,
    val restaurant: RestaurantDTO? = null,
    val tableId: Int? = null,
    val participants: List<UserSummaryDTO>? = null,
    val participantIds: List<String>? = null,
    /***
     * List of orders containing: orderId, visitId, date, note, cost, status
     */
    val orders: List<OrderDTO>? = null,
    val isArchived: Boolean? = null,
    val restaurantId: Int? = null,
    val createdByEmployee: Boolean? = null,
    val isAccepted: Boolean? = null
)