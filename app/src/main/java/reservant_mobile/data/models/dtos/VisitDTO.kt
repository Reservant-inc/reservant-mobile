package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class VisitDTO(
    val visitId: Int? = null,
    /***
     * Date in 'yyyy-MM-d'T'H:mm:ss.SSS'Z'' format
     */
    val date: String? = null,
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
    val restaurantId: Int? = null,
    val tableId: Int? = null,
    /***
     * List of users containing: userId, firstName, lastname
     */
    val participants: List<UserDTO>? = null,
    /***
     * List of orders containing: orderId, visitId, date, note, cost, status
     */
    val orders: List<OrderDTO>? = null
)