package reservant_mobile.data.models.dtos
import kotlinx.serialization.Serializable

@Serializable
data class OrderDTO(
    val orderId: Int? = null,
    val visitId: Int? = null,
    val cost: Double? = null,
    /***
     * Date in 'yyyy-MM-d'T'H:mm:ss.SS' format
     */
    val date: String? = null,
    val status: String? = null,
    val note: String? = null,
    val items: List<OrderItemDTO>? = null,
    val employeeId: String? = null
){
    @Serializable
    data class OrderItemDTO(
        val menuItemId: Int,
        val amount: Int,
        val cost: Double? = null,
        val status: String? = null
    )
}