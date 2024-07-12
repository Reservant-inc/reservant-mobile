package reservant_mobile.data.models.dtos
import kotlinx.serialization.Serializable

@Serializable
data class OrderDTO(
    val orderId: Int? = null,
    val visitId: Int? = null,
    val cost: String? = null,
//    val date: String? = null,
    val status: String? = null,
    val note: String? = null,
    val items: List<OrderItemsDTO>,
    val employeeId: String? = null
){
    @Serializable
    data class OrderItemsDTO(
        val menuItemId: Int,
        val amount: Int,
        val cost: Double? = null,
        val status: String? = null
    )
}