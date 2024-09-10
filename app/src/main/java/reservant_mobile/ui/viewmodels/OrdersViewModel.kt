import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.ui.viewmodels.ReservantViewModel

class OrdersViewModel : ReservantViewModel() {

    private val _orders = MutableStateFlow<List<OrderDTO>>(emptyList())
    val orders: StateFlow<List<OrderDTO>> = _orders.asStateFlow()

    private val _filteredOrders = MutableStateFlow<List<OrderDTO>>(emptyList())
    val filteredOrders: StateFlow<List<OrderDTO>> = _filteredOrders.asStateFlow()

    private var allOrders: List<OrderDTO> = emptyList()

    init {
        viewModelScope.launch { fetchOrders() }
    }

    private suspend fun fetchOrders() {
        val fetchedOrders = emptyList<OrderDTO>()
        allOrders = fetchedOrders
        _orders.value = fetchedOrders
        _filteredOrders.value = fetchedOrders
    }

    fun searchOrders(query: String) {
        _filteredOrders.value = if (query.isBlank()) {
            allOrders
        } else {
            // TODO: uncomment when OrderDTO will contain customer info
            // allOrders.filter { it.customer.contains(query, ignoreCase = true) }
            emptyList()
        }
    }

    fun filterOrders(status: String?) {
        _filteredOrders.value = if (status.isNullOrBlank()) {
            allOrders
        } else {
            allOrders.filter { it.status == status }
        }
    }
}
