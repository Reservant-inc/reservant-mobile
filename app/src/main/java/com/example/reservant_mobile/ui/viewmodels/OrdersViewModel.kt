import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.OrderDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrdersViewModel : ViewModel() {

    private val _orders = MutableStateFlow<List<OrderDTO>>(emptyList())
    val orders: StateFlow<List<OrderDTO>> = _orders.asStateFlow()

    private val _filteredOrders = MutableStateFlow<List<OrderDTO>>(emptyList())
    val filteredOrders: StateFlow<List<OrderDTO>> = _filteredOrders.asStateFlow()

    private var allOrders: List<OrderDTO> = emptyList()

    init {
        viewModelScope.launch { fetchOrders() }
    }

    private suspend fun fetchOrders() {
        val fetchedOrders = listOf(
            OrderDTO("10-05-2024", "90 PLN", "Jan Kowalski", "Odebrano"),
            OrderDTO("11-05-2024", "150 PLN", "Anna Nowak", "Anulowano"),
            OrderDTO("12-05-2024", "200 PLN", "Piotr Wiśniewski", "Odebrano"),
            OrderDTO("13-05-2024", "120 PLN", "Katarzyna Zielińska", "Odebrano"),
            OrderDTO("14-05-2024", "130 PLN", "Marcin Lewandowski", "Anulowano"),
            OrderDTO("15-05-2024", "80 PLN", "Sylwia Wójcik", "Odebrano"),
            OrderDTO("16-05-2024", "170 PLN", "Krzysztof Krawczyk", "Odebrano"),
            OrderDTO("17-05-2024", "110 PLN", "Magdalena Kaczmarek", "Anulowano"),
            OrderDTO("18-05-2024", "140 PLN", "Rafał Piotrowski", "Odebrano"),
            OrderDTO("19-05-2024", "190 PLN", "Agnieszka Kowal", "Odebrano")
        )
        allOrders = fetchedOrders
        _orders.value = fetchedOrders
        _filteredOrders.value = fetchedOrders
    }

    fun searchOrders(query: String) {
        _filteredOrders.value = if (query.isBlank()) {
            allOrders
        } else {
            allOrders.filter { it.customer.contains(query, ignoreCase = true) }
        }
    }

    fun filterOrders(status: String) {
        _filteredOrders.value = allOrders.filter { it.status == status }
    }
}
