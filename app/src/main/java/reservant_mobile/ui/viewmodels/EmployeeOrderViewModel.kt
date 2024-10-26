package reservant_mobile.ui.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.RestaurantService

class EmployeeOrderViewModel(
    private val restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService()
) : ReservantViewModel() {

    // State for current orders
    private val _currentOrders = MutableStateFlow<Flow<PagingData<OrderDTO>>?>(null)
    val currentOrders: StateFlow<Flow<PagingData<OrderDTO>>?> = _currentOrders

    // State for past orders
    private val _pastOrders = MutableStateFlow<Flow<PagingData<OrderDTO>>?>(null)
    val pastOrders: StateFlow<Flow<PagingData<OrderDTO>>?> = _pastOrders

    init {
        fetchCurrentOrders()
        fetchPastOrders()
    }

    // Fetch current (unfinished) orders
    fun fetchCurrentOrders() {
        viewModelScope.launch {
            val result: Result<Flow<PagingData<OrderDTO>>?> = restaurantService.getRestaurantOrders(
                restaurantId = restaurantId,
                returnFinished = false,
                orderBy = null
            )

            if (!result.isError) {
                _currentOrders.value = result.value?.cachedIn(viewModelScope)
            }
        }
    }

    // Fetch past (finished) orders
    fun fetchPastOrders() {
        viewModelScope.launch {
            val result: Result<Flow<PagingData<OrderDTO>>?> = restaurantService.getRestaurantOrders(
                restaurantId = restaurantId,
                returnFinished = true,
                orderBy = null
            )

            if (!result.isError) {
                _pastOrders.value = result.value?.cachedIn(viewModelScope)
            }
        }
    }
}
