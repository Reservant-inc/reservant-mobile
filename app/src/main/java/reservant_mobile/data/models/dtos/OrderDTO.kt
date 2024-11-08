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
    val status: OrderStatus? = null,
    val note: String? = null,
    val items: List<OrderItemDTO>? = null,
    val employeeId: String? = null,
    val assignedEmployee: UserSummaryDTO? = null

){
    @Serializable
    data class OrderItemDTO(
        val menuItem: RestaurantMenuItemDTO? = null,
        var menuItemId: Int? = null,
        val amount: Int? = null,
        val oneItemPrice: Double? = null,
        val totalCost: Double? = null,
        val status: String? = null
    )

    @Serializable
    enum class OrderStatus{
        InProgress,
        Ready,
        Taken,
        Cancelled
    }
}