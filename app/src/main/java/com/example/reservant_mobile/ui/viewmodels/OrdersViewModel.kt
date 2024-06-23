import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.OrderDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrdersViewModel : ViewModel() {

    private val _orders = MutableStateFlow<List<OrderDTO>>(emptyList())
    val orders: StateFlow<List<OrderDTO>> = _orders

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            val fetchedOrders = listOf(
                OrderDTO("10-05-2024", "90 PLN", "Jan Kowalski", "Odebrano"),
                OrderDTO("10-05-2024", "90 PLN", "Jan Kowalski", "Odebrano"),
                OrderDTO("10-05-2024", "90 PLN", "Jan Kowalski", "Odebrano"),
                OrderDTO("10-05-2024", "90 PLN", "Jan Kowalski", "Odebrano")
            )
            _orders.value = fetchedOrders
        }
    }
}
