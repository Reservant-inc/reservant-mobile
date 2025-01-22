package reservant_mobile.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.reservant_mobile.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.data.models.dtos.FriendRequestDTO
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.DeliveryService
import reservant_mobile.data.services.FriendsService
import reservant_mobile.data.services.IDeliveryService
import reservant_mobile.data.services.IFriendsService
import reservant_mobile.data.services.IOrdersService
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.IVisitsService
import reservant_mobile.data.services.OrdersService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.services.VisitsService
import reservant_mobile.data.utils.ResourceProvider
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ReservationViewModel(
    private val ordersService: IOrdersService = OrdersService(),
    private val visitsService: IVisitsService = VisitsService(),
    private val deliveryService: IDeliveryService = DeliveryService(),
    private val resourceProvider: ResourceProvider,
    private val restaurantService: IRestaurantService = RestaurantService(),
    private val friendsService: IFriendsService = FriendsService(),
) : ReservantViewModel() {

    var note: FormField = FormField(OrderDTO::note.name)
    var promoCode: FormField = FormField("promoCode")
    var orderCost by mutableStateOf(0.0)
    var visitDate: FormField = FormField("VisitDate").apply { value = LocalDate.now().toString() }
    var startTime: FormField = FormField(VisitDTO::reservationDate.name)
    var endTime: FormField = FormField(VisitDTO::endTime.name)
    var totalGuests by mutableStateOf(1)
    var tip by mutableStateOf(0.0)
    var visitId by mutableStateOf(0)

    val addedItems = mutableStateListOf<Pair<RestaurantMenuItemDTO, Int>>()
    var participantIds: MutableList<String> = mutableListOf()

    var isTakeaway by mutableStateOf(false)
    var isDelivery by mutableStateOf(false)
    var isDepositPaid by mutableStateOf(false)
    var deliveryAddress: FormField = FormField("deliveryAddress")
    var deliveryCost by mutableStateOf(0.0)

    var returnedVisit by mutableStateOf<VisitDTO?>(null)

    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isDateError by mutableStateOf(false)
        private set
    var dateErrorText by mutableStateOf<String?>(null)
        private set
    var isStartTimeError by mutableStateOf(false)
        private set
    var startTimeErrorText by mutableStateOf<String?>(null)
        private set
    var isEndTimeError by mutableStateOf(false)
        private set
    var endTimeErrorText by mutableStateOf<String?>(null)
        private set

    private val _orderResult = MutableStateFlow<Result<OrderDTO?>>(Result(isError = false, value = null))
    val orderResult: StateFlow<Result<OrderDTO?>> = _orderResult

    private val _visitResult = MutableStateFlow<Result<VisitDTO?>>(Result(isError = false, value = null))
    val visitResult: StateFlow<Result<VisitDTO?>> = _visitResult

    private val _deliveryResult = MutableStateFlow<Result<DeliveryDTO?>>(Result(isError = false, value = null))
    val deliveryResult: StateFlow<Result<DeliveryDTO?>> = _deliveryResult

    private val _friendsFlow = MutableStateFlow<Flow<PagingData<FriendRequestDTO>>?>(null)
    val friendsFlow: Flow<PagingData<FriendRequestDTO>>?
        get() = _friendsFlow.value

    var restaurant: RestaurantDTO? by mutableStateOf(null)

    suspend fun getPhoto(url: String): Bitmap?{
        val result = fileService.getImage(url)
        if (!result.isError){
            return result.value!!
        }
        return null
    }
    fun loadFriendsPaging() {
        viewModelScope.launch {
            val result = friendsService.getFriends()
            if (!result.isError && result.value != null) {
                // Store the paging Flow
                _friendsFlow.value = result.value.cachedIn(viewModelScope)
            } else {
                // If error, set to null or handle it
                _friendsFlow.value = null
            }
        }
    }
    suspend fun getRestaurant(restaurantId: Int){
        val result = restaurantService.getRestaurant(restaurantId)

        if(!result.isError){
            restaurant = result.value
        }
    }

    private fun roundUpToNextHalfHour(time: LocalTime): LocalTime {
        val minute = time.minute
        return when {
            minute == 0 || minute == 30 -> time
            minute < 30 -> time.withMinute(30).withSecond(0).withNano(0)
            else -> time.plusHours(1).withMinute(0).withSecond(0).withNano(0)
        }
    }

    suspend fun payDeposit(){
        val result = visitsService.payDeposit(returnedVisit!!.visitId.toString())

        if(!result.isError){
            isDepositPaid = true
        }
    }

    fun updateDate(dateString: String, restaurant: RestaurantDTO) {
        visitDate.value = dateString
        isDateError = false
        dateErrorText = null

        if (dateString.isEmpty()) {
            isDateError = true
            dateErrorText = resourceProvider.getString(R.string.error_date_not_selected)
            return
        }

        try {
            val selectedDate = LocalDate.parse(dateString)
            val today = LocalDate.now()

            if (selectedDate.isBefore(today)) {
                isDateError = true
                dateErrorText = resourceProvider.getString(R.string.error_date_past)
                return
            }

            val dayIndex = selectedDate.dayOfWeek.value - 1
            val dayHours = restaurant.openingHours?.getOrNull(dayIndex)

            if (dayHours?.from == null || dayHours.until == null) {
                isDateError = true
                dateErrorText = resourceProvider.getString(R.string.error_restaurant_closed)
            }

        } catch (e: Exception) {
            isDateError = true
            dateErrorText = resourceProvider.getString(R.string.error_invalid_date_format)
        }
    }

    fun updateStartTime(timeString: String, restaurant: RestaurantDTO) {
        try {
            isStartTimeError = false
            val inputTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
            val roundedTime = roundUpToNextHalfHour(inputTime)
            startTime.value = roundedTime.format(DateTimeFormatter.ofPattern("HH:mm"))

            val selectedDate = LocalDate.parse(visitDate.value)
            val now = LocalTime.now()
            if (selectedDate.isEqual(LocalDate.now()) && roundedTime.isBefore(now)) {
                isStartTimeError = true
                startTimeErrorText = resourceProvider.getString(R.string.error_start_time_before_now)
                return
            }

            val dayIndex = selectedDate.dayOfWeek.value - 1
            val dayHours = restaurant.openingHours?.getOrNull(dayIndex)

            if (dayHours?.from == null || dayHours.until == null) {
                isStartTimeError = true
                startTimeErrorText = resourceProvider.getString(R.string.error_restaurant_closed)
                return
            }

            val ohFrom = LocalTime.parse(dayHours.from, DateTimeFormatter.ofPattern("HH:mm"))
            val ohUntil = LocalTime.parse(dayHours.until, DateTimeFormatter.ofPattern("HH:mm"))

            if (roundedTime.isBefore(ohFrom) || roundedTime.isAfter(ohUntil)) {
                isStartTimeError = true
                startTimeErrorText = resourceProvider.getString(R.string.error_start_time_outside_opening_hours)
            }

        } catch (e: Exception) {
            isStartTimeError = true
            startTimeErrorText = resourceProvider.getString(R.string.error_invalid_date_format)
        }
    }

    fun updateEndTime(timeString: String, restaurant: RestaurantDTO, skipRounding: Boolean = false) {
        try {
            isEndTimeError = false
            val inputTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
            val finalEndTime = if (skipRounding) inputTime else roundUpToNextHalfHour(inputTime)
            endTime.value = finalEndTime.format(DateTimeFormatter.ofPattern("HH:mm"))

            val selectedDate = LocalDate.parse(visitDate.value)
            val start = LocalTime.parse(startTime.value, DateTimeFormatter.ofPattern("HH:mm"))

            if (!finalEndTime.isAfter(start)) {
                isEndTimeError = true
                endTimeErrorText = resourceProvider.getString(R.string.error_end_time_before_start)
                return
            }

            val diffMinutes = java.time.Duration.between(start, finalEndTime).toMinutes()
            val maxDuration = restaurant.maxReservationDurationMinutes ?: 90

            if (diffMinutes > maxDuration) {
                isEndTimeError = true
                endTimeErrorText = resourceProvider.getString(
                    R.string.error_end_time_exceeded_length,
                    maxDuration
                )
                return
            }

            val dayIndex = selectedDate.dayOfWeek.value - 1
            val dayHours = restaurant.openingHours?.getOrNull(dayIndex)

            if (dayHours?.from == null || dayHours.until == null) {
                isEndTimeError = true
                endTimeErrorText = resourceProvider.getString(R.string.error_restaurant_closed)
                return
            }

            val ohFrom = LocalTime.parse(dayHours.from, DateTimeFormatter.ofPattern("HH:mm"))
            val ohUntil = LocalTime.parse(dayHours.until, DateTimeFormatter.ofPattern("HH:mm"))
            if (finalEndTime.isBefore(ohFrom) || finalEndTime.isAfter(ohUntil)) {
                isEndTimeError = true
                endTimeErrorText = resourceProvider.getString(R.string.error_end_time_outside_opening_hours)
            }

        } catch (e: Exception) {
            isEndTimeError = true
            endTimeErrorText = resourceProvider.getString(R.string.error_invalid_date_format)
        }
    }

    fun isReservationValid(isReservation: Boolean): Boolean {
        return !(isDateError || isStartTimeError || isEndTimeError || isTipError() || (!isReservation && isCartEmpty()))
    }

    fun addItemToCart(menuItem: RestaurantMenuItemDTO) {
        val existingItem = addedItems.find { it.first.menuItemId == menuItem.menuItemId }
        if (existingItem != null) {
            val index = addedItems.indexOf(existingItem)
            addedItems[index] = existingItem.copy(second = existingItem.second + 1)
        } else {
            addedItems.add(menuItem to 1)
        }
    }

    fun removeItemFromCart(item: Pair<RestaurantMenuItemDTO, Int>) {
        addedItems.remove(item)
    }

    fun increaseItemQuantity(item: Pair<RestaurantMenuItemDTO, Int>) {
        val index = addedItems.indexOfFirst { it.first.menuItemId == item.first.menuItemId }
        if (index >= 0) {
            addedItems[index] = item.copy(second = item.second + 1)
        }
    }

    fun decreaseItemQuantity(item: Pair<RestaurantMenuItemDTO, Int>) {
        val index = addedItems.indexOfFirst { it.first.menuItemId == item.first.menuItemId }
        if (index >= 0) {
            val newQuantity = item.second - 1
            if (newQuantity > 0) {
                addedItems[index] = item.copy(second = newQuantity)
            } else {
                addedItems.removeAt(index)
            }
        }
    }

    suspend fun createOrder(): Result<OrderDTO?> {
        // 1) Build the order items
        val orderItems = addedItems.map { (menuItem, quantity) ->
            OrderDTO.OrderItemDTO(
                menuItemId = menuItem.menuItemId,
                amount = quantity
            )
        }

        // 2) Create the OrderDTO
        val noteValue = note.value.takeIf { it.isNotBlank() }
        val order = OrderDTO(
            visitId = visitId,
            note = noteValue,
            items = orderItems
        )

        // 3) Call the service
        val orderResult = ordersService.createOrder(order)
        _orderResult.value = orderResult

        // 4) If there's an error, show a toast (optional)
        if (orderResult.isError) {
            resourceProvider.showToast(resourceProvider.getString(R.string.error_place_order))
        } else {
            // If success, clear cart or do any other logic
            addedItems.clear()
            errorMessage = null
        }

        // 5) Return the result to the caller
        return orderResult
    }


    suspend fun createVisitAndOrder(restaurantId: Int): Result<Pair<VisitDTO?, OrderDTO?>> {
        // 1) Attempt to create the Visit
        val visitRes = createVisit(restaurantId)
        if (visitRes.isError) {
            // Return an error => do not create the order
            return Result(
                isError = true,
                errors = visitRes.errors,
                value = Pair(null, null)
            )
        }

        // 2) If visit succeeded, create the order
        val orderRes = createOrder()  // Also returns Result<OrderDTO?>
        if (orderRes.isError) {
            // If the order fails, we can optionally roll back or just fail
            return Result(
                isError = true,
                errors = orderRes.errors,
                value = Pair(null, null)
            )
        }

        // 3) If both succeeded, return them in a pair
        val combinedValue = Pair(visitRes.value, orderRes.value)
        return Result(
            isError = false,
            errors = emptyMap(),
            value = combinedValue
        )
    }


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

    suspend fun createVisit(restaurantId: Int): Result<VisitDTO?> {
        val visit = VisitDTO(
            date = "${visitDate.value}T${startTime.value}",
            endTime = "${visitDate.value}T${endTime.value}",
            numberOfGuests = totalGuests-participantIds.size-1,
            tip = tip,
            takeaway = isTakeaway,
            restaurantId = restaurantId,
            participantIds = participantIds
        )
        val visitResult = visitsService.createVisit(visit)
        _visitResult.value = visitResult

        if (visitResult.isError) {
            if(visitResult.errors != null) {
                //TODO zmienić w przyszłości
                val errorMap = visitResult.errors
                if (errorMap != null && errorMap.containsValue(R.string.errorCode_Duplicate)) {
                    resourceProvider.showToast(resourceProvider.getString(R.string.reservation_conflict_message))
                }
                if (errorMap != null && errorMap.containsValue(R.string.errorCode_NoAvailableTable)) {
                    resourceProvider.showToast(resourceProvider.getString(R.string.errorCode_NoAvailableTable))
                }
            }
            resourceProvider.showToast(resourceProvider.getString(R.string.error_create_visit))
        } else {
            visitId = visitResult.value!!.visitId!!
            errorMessage = null
        }
        return visitResult
    }

    fun getVisit(visitId: Any) {
        viewModelScope.launch {
            val result = visitsService.getVisit(visitId)
            _visitResult.value = result
        }
    }

    fun addDelivery() {
        viewModelScope.launch {
            val delivery = DeliveryDTO(
                cost = deliveryCost
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

    fun isOrderError(): Boolean = _orderResult.value.isError
    fun isVisitError(): Boolean = _visitResult.value.isError
    fun isDeliveryError(): Boolean = _deliveryResult.value.isError

    fun isTipError(): Boolean {
        return tip < 0
    }

    fun isCartEmpty(): Boolean {
        return addedItems.isEmpty()
    }
}
