package reservant_mobile.ui.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.services.IOrdersService
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.OrdersService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.GetReservationStatus
import java.time.LocalDateTime

class EmployeeOrderViewModel(
    private val restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService(),
    private val userService: IUserService = UserService(),
    private val ordersService: IOrdersService = OrdersService()
) : ReservantViewModel() {

    private val visitCache = mutableMapOf<Int, VisitDTO>()

    val currentVisits: Flow<PagingData<VisitDTO>> = flow {
        val result = restaurantService.getVisits(
            restaurantId = restaurantId,
            dateStart = LocalDateTime.now(),
            dateEnd = null,
            orderBy = null,
            reservationStatus = GetReservationStatus.Approved
        )
        if (!result.isError && result.value != null) {
            emitAll(result.value.cachedIn(viewModelScope))
        } else {
            emit(PagingData.empty())
        }
    }.catch {
        emit(PagingData.empty())
    }

    val pastVisits: Flow<PagingData<VisitDTO>> = flow {
        val result = restaurantService.getVisits(
            restaurantId = restaurantId,
            dateStart = null,
            dateEnd = LocalDateTime.now(),
            orderBy = null,
            reservationStatus = GetReservationStatus.Approved
        )
        if (!result.isError && result.value != null) {
            emitAll(result.value.cachedIn(viewModelScope))
        } else {
            emit(PagingData.empty())
        }
    }.catch {
        emit(PagingData.empty())
    }

    private val _selectedVisitDetails = MutableStateFlow<VisitDetailsUIState?>(null)
    val selectedVisitDetails: StateFlow<VisitDetailsUIState?> = _selectedVisitDetails.asStateFlow()

    fun fetchVisitDetailsById(visitId: Int) {
        val visit = visitCache[visitId] ?: return
        fetchVisitDetails(visit)
    }

    fun cacheVisit(visit: VisitDTO) {
        visit.visitId?.let {
            visitCache[it] = visit
        }
    }

    private fun fetchVisitDetails(visit: VisitDTO) {
        viewModelScope.launch {
            val userSummary = userService.getUserSimpleInfo(visit.clientId ?: "")
            val clientName = "${userSummary.value?.firstName ?: ""} ${userSummary.value?.lastName ?: ""}".trim()
            
            val fullOrders = visit.orders?.mapNotNull { partialOrder ->
                val orderResult = partialOrder.orderId?.let { ordersService.getOrder(it) }
                if (!orderResult?.isError!!) orderResult.value else null
            } ?: emptyList()

            val updatedVisit = visit.copy(orders = fullOrders)

            _selectedVisitDetails.value = VisitDetailsUIState(
                clientName = clientName,
                visit = updatedVisit
            )
        }
    }

}

data class VisitDetailsUIState(
    val clientName: String,
    val visit: VisitDTO
)
