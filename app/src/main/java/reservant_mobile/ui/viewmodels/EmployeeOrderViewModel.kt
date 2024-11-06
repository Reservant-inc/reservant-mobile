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
import reservant_mobile.data.services.IVisitsService
import reservant_mobile.data.services.OrdersService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.services.VisitsService
import reservant_mobile.data.utils.GetReservationStatus
import java.time.LocalDateTime

// Import statements remain the same...

class EmployeeOrderViewModel(
    private val restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService(),
    private val userService: IUserService = UserService(),
    private val ordersService: IOrdersService = OrdersService(),
    private val visitsService: IVisitsService = VisitsService()
) : ReservantViewModel() {

    private val visitCache = mutableMapOf<Int, VisitDTO>()

    fun getVisitsFlow(
        dateStart: LocalDateTime? = null,
        dateEnd: LocalDateTime? = null,
        reservationStatus: GetReservationStatus? = null
    ): Flow<PagingData<VisitDTO>> = flow {
        val result = restaurantService.getVisits(
            restaurantId = restaurantId,
            dateStart = dateStart,
            dateEnd = dateEnd,
            reservationStatus = reservationStatus
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
                if (orderResult != null && !orderResult.isError) orderResult.value else null
            } ?: emptyList()

            val updatedVisit = visit.copy(orders = fullOrders)

            _selectedVisitDetails.value = VisitDetailsUIState(
                clientName = clientName,
                visit = updatedVisit
            )
        }
    }

    fun approveVisit(visitId: Int) {
        viewModelScope.launch {
            val result = visitsService.approveVisit(visitId)
            if (!result.isError) {
                // Optionally, refresh data or update UI
            } else {
                // Handle error
            }
        }
    }

    fun declineVisit(visitId: Int) {
        viewModelScope.launch {
            val result = visitsService.declineVisit(visitId)
            if (!result.isError) {
                // Optionally, refresh data or update UI
            } else {
                // Handle error
            }
        }
    }
}

data class VisitDetailsUIState(
    val clientName: String,
    val visit: VisitDTO
)
