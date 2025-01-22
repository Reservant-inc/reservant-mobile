import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.IOrdersService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.IVisitsService
import reservant_mobile.data.services.OrdersService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.services.VisitsService
import reservant_mobile.ui.viewmodels.ReservantViewModel

class VisitHistoryViewModel(
    private val userService: IUserService = UserService(),
    private val visitsService: IVisitsService = VisitsService(),
    private val ordersService: IOrdersService = OrdersService()
) : ReservantViewModel() {

    // For PAST visits
    private val _visitHistoryFlow = MutableStateFlow<Flow<PagingData<VisitDTO>>?>(null)
    val visitHistoryFlow: Flow<PagingData<VisitDTO>>?
        get() = _visitHistoryFlow.value

    // For UPCOMING visits
    private val _futureVisitsFlow = MutableStateFlow<Flow<PagingData<VisitDTO>>?>(null)
    val futureVisitsFlow: Flow<PagingData<VisitDTO>>?
        get() = _futureVisitsFlow.value

    // Single-visit detail
    private val _visit = MutableStateFlow<VisitDTO?>(null)
    val visit: StateFlow<VisitDTO?> = _visit.asStateFlow()

    private val _order = MutableStateFlow<OrderDTO?>(null)
    val order: StateFlow<OrderDTO?> = _order.asStateFlow()

    // Optional error or loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _ordersMap = MutableStateFlow<Map<Int, OrderDTO?>>(emptyMap())
    val ordersMap: StateFlow<Map<Int, OrderDTO?>> = _ordersMap.asStateFlow()

    fun loadSingleOrder(orderId: Int) {
        viewModelScope.launch {
            val result = ordersService.getOrder(orderId)
            if (!result.isError && result.value != null) {
                _ordersMap.value = _ordersMap.value.toMutableMap().apply {
                    this[orderId] = result.value
                }
            }
        }
    }

    fun loadOrder(orderId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result: Result<OrderDTO?> = ordersService.getOrder(orderId)
            _isLoading.value = false

            if (!result.isError && result.value != null) {
                _order.value = result.value
            } else {
                // show error
                _errorMessage.value = "Failed to fetch order #$orderId"
                _order.value = null
            }
        }
    }

    fun loadVisitHistory() {
        viewModelScope.launch {
            val result = userService.getUserVisitHistory()
            if (!result.isError && result.value != null) {
                _visitHistoryFlow.value = result.value.cachedIn(viewModelScope)
            } else {
                _visitHistoryFlow.value = null
            }
        }
    }

    fun loadUpcomingVisits() {
        viewModelScope.launch {
            val result = userService.getUserVisits()
            if (!result.isError && result.value != null) {
                _futureVisitsFlow.value = result.value.cachedIn(viewModelScope)
            } else {
                _futureVisitsFlow.value = null
            }
        }
    }

    fun loadVisit(visitId: Int) {
        viewModelScope.launch {
            val result = visitsService.getVisit(visitId)
            if (!result.isError && result.value != null) {
                _visit.value = result.value
            } else {
                _visit.value = null
            }
        }
    }
}
