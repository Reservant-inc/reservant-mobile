package reservant_mobile.ui.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.services.IOrdersService
import reservant_mobile.data.services.IRestaurantMenuService
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.OrdersService
import reservant_mobile.data.services.RestaurantMenuService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.services.UserService
import java.time.LocalDateTime

class EmployeeOrderViewModel(
    private val restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService(),
    private val userService: IUserService = UserService(),
    private val menuService: IRestaurantMenuService = RestaurantMenuService(),
    private val ordersService: IOrdersService = OrdersService()
) : ReservantViewModel() {

    private val visitCache = mutableMapOf<Int, VisitDTO>()

    val currentVisits: Flow<PagingData<VisitDTO>> = flow {
        val result = restaurantService.getVisits(
            restaurantId = restaurantId,
            dateStart = LocalDateTime.now(),
            dateEnd = null,
            orderBy = null
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
            orderBy = null
        )

        if (!result.isError && result.value != null) {
            emitAll(result.value.cachedIn(viewModelScope))
        } else {
            emit(PagingData.empty())
        }
    }.catch { exception ->
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
            val visitDetails = mutableListOf<OrderDetails>()
            val userSummary = userService.getUserSimpleInfo(visit.clientId ?: "")
            val participants = visit.participants
            visit.orders?.forEach { order ->
                val orderResult = ordersService.getOrder(order.orderId!!)
                val fetchedOrder = orderResult.value

                if (fetchedOrder != null) {
                    val items = fetchedOrder.items?.mapNotNull { item ->
                        val menuItem = menuService.getMenuItem(item.menuItemId).value
                        menuItem?.let {
                            OrderDetails.MenuItemDetails(
                                name = it.name,
                                price = it.price,
                                amount = item.amount,
                                status = item.status,
                                cost = item.cost ?: -1.0
                            )
                        }
                    }.orEmpty()

                    visitDetails.add(
                        OrderDetails(
                            orderId = fetchedOrder.orderId ?: -1,
                            items = items,
                            cost = fetchedOrder.cost ?: -1.0
                        )
                    )
                }
            }

            _selectedVisitDetails.value = VisitDetailsUIState(
                clientName = "${userSummary.value?.firstName} ${userSummary.value?.lastName}",
                participants = participants.takeIf { it!!.isNotEmpty() }?.map { "${it.firstName} ${it.lastName}" } ?: emptyList(),
                orders = visitDetails,
                totalCost = visit.orders?.sumOf { it.cost ?: -1.0 } ?: -1.0,
                tableId = visit.tableId ?: -1,
                numberOfPeople = when {
                    visit.participants?.isNotEmpty() == true && visit.numberOfGuests != null -> visit.numberOfGuests + visit.participants.size + 1
                    visit.participants?.isNotEmpty() == true -> visit.participants.size + 1
                    visit.numberOfGuests != null -> visit.numberOfGuests + 1
                    else -> 1 },
                tip = visit.tip ?: -1.0,
                date = visit.date ?: "Unknown",
                endTime = visit.endTime ?: "Unknown",
                paymentTime = visit.paymentTime  ?: "Unknown",
                deposit = visit.deposit ?: -1.0,
                reservationDate = visit.reservationDate  ?: "Unknown",
                takeaway = visit.takeaway
            )
        }
    }.catch { exception ->
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

    fun fetchVisitDetails(visit: VisitDTO) {
        viewModelScope.launch {
            val visitDetails = mutableListOf<OrderDetails>()
            val userSummary = userService.getUserSimpleInfo(visit.clientId ?: "")
            val participants = visit.participantIds?.mapNotNull { userId ->
                userService.getUserSimpleInfo(userId).value
            }.orEmpty()

            visit.orders?.forEach { order ->
                val orderResult = ordersService.getOrder(order.orderId!!)
                val fetchedOrder = orderResult.value

                if (fetchedOrder != null) {
                    val items = fetchedOrder.items?.mapNotNull { item ->
                        val menuItem = menuService.getMenuItem(item.menuItemId).value
                        menuItem?.let {
                            OrderDetails.MenuItemDetails(
                                name = it.name,
                                price = it.price,
                                amount = item.amount,
                                status = item.status
                            )
                        }
                    }.orEmpty()

                    visitDetails.add(
                        OrderDetails(
                            orderId = fetchedOrder.orderId ?: 0,
                            items = items
                        )
                    )
                }
            }

            _selectedVisitDetails.value = VisitDetailsUIState(
                clientName = "${userSummary.value?.firstName} ${userSummary.value?.lastName}",
                participants = participants.map { "${it.firstName} ${it.lastName}" },
                orders = visitDetails,
                totalCost = visit.orders?.sumOf { it.cost ?: 0.0 } ?: 0.0,
                tableId = visit.tableId ?: -1
            )
        }
    }
}

data class VisitDetailsUIState(
    val clientName: String,
    val participants: List<String>,
    val orders: List<OrderDetails>,
    val totalCost: Double,
    val tableId: Int,
    val numberOfPeople: Int,
    val tip: Double,
    val date: String,
    val endTime: String,
    val paymentTime: String,
    val deposit: Double,
    val reservationDate: String,
    val takeaway: Boolean?,
)

data class OrderDetails(
    val orderId: Int,
    val items: List<MenuItemDetails>,
    val cost: Double
) {
    data class MenuItemDetails(
        val name: String,
        val price: Double,
        val amount: Int,
        val status: String?,
        val cost: Double
    )
}
