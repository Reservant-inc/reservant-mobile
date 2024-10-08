package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.DeliveryService
import reservant_mobile.data.services.IDeliveryService
import reservant_mobile.data.services.IOrdersService
import reservant_mobile.data.services.IVisitsService
import reservant_mobile.data.services.OrdersService
import reservant_mobile.data.services.VisitsService

class ReservationViewModel(
    private val ordersService: IOrdersService = OrdersService(),
    private val visitsService: IVisitsService = VisitsService(),
    private val deliveryService: IDeliveryService = DeliveryService()
) : ReservantViewModel() {


    var note: FormField = FormField(OrderDTO::note.name)
    var promoCode: FormField = FormField("promoCode") // Assuming promoCode is a custom field
    var orderCost by mutableDoubleStateOf(0.0)


    var visitDate: FormField = FormField(VisitDTO::reservationDate.name)
    var numberOfGuests by mutableIntStateOf(1)
    var seats by mutableIntStateOf(1)


    var deliveryAddress: FormField = FormField("deliveryAddress")
    var deliveryCost by mutableDoubleStateOf(0.0)

    // Results
    private val _orderResult = MutableStateFlow<Result<OrderDTO?>>(Result(isError = false, value = null))
    val orderResult: StateFlow<Result<OrderDTO?>> = _orderResult

    private val _visitResult = MutableStateFlow<Result<VisitDTO?>>(Result(isError = false, value = null))
    val visitResult: StateFlow<Result<VisitDTO?>> = _visitResult

    private val _deliveryResult = MutableStateFlow<Result<DeliveryDTO?>>(Result(isError = false, value = null))
    val deliveryResult: StateFlow<Result<DeliveryDTO?>> = _deliveryResult

    // Functions to handle orders
    fun createOrder() {
        viewModelScope.launch {
            val order = OrderDTO(
                cost = orderCost,
                note = note.value
                // Include other necessary fields
            )
            val result = ordersService.createOrder(order)
            _orderResult.value = result
        }
    }

    // Functions to handle orders
    private fun getOrder(orderId: Any) {
        viewModelScope.launch {
            val result = ordersService.getOrder(orderId)
            _orderResult.value = result
        }
    }

    fun cancelOrder(orderId: Any) {
        viewModelScope.launch {
            val result = ordersService.cancelOrder(orderId)
            _orderResult.value = Result(isError = result.isError, value = null)
        }
    }

    fun changeOrderStatus(orderId: Any, order: OrderDTO) {
        viewModelScope.launch {
            val result = ordersService.changeOrderStatus(orderId, order)
            _orderResult.value = result
        }
    }

    // Functions to handle visits
    fun createVisit() {
        viewModelScope.launch {
            val visit = VisitDTO(
                reservationDate = visitDate.value,
                numberOfGuests = numberOfGuests,
                // Include other necessary fields
            )
            val result = visitsService.createVisit(visit)
            _visitResult.value = result
        }
    }

    fun getVisit(visitId: Any) {
        viewModelScope.launch {
            val result = visitsService.getVisit(visitId)
            _visitResult.value = result
        }
    }

    // Functions to handle deliveries
    fun addDelivery() {
        viewModelScope.launch {
            val delivery = DeliveryDTO(
                cost = deliveryCost,
                // Include other necessary fields
            )
            val result = deliveryService.addDelivery(delivery)
            _deliveryResult.value = result
        }
    }

    fun getDelivery(deliveryId: Int) {
        viewModelScope.launch {
            val result = deliveryService.getDelivery(deliveryId)
            _deliveryResult.value = result
        }
    }

    // Helper functions to determine if there is an error in the operation results
    fun isOrderError(): Boolean = _orderResult.value.isError
    fun isVisitError(): Boolean = _visitResult.value.isError
    fun isDeliveryError(): Boolean = _deliveryResult.value.isError
}
