package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.DeliveryService
import reservant_mobile.data.services.IDeliveryService
import reservant_mobile.data.services.IOrdersService
import reservant_mobile.data.services.IVisitsService
import reservant_mobile.data.services.OrdersService
import reservant_mobile.data.services.VisitsService
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ReservationViewModel(
    private val ordersService: IOrdersService = OrdersService(),
    private val visitsService: IVisitsService = VisitsService(),
    private val deliveryService: IDeliveryService = DeliveryService()
) : ReservantViewModel() {

    // Pola powiązane z formularzem
    var note: FormField = FormField(OrderDTO::note.name)
    var promoCode: FormField = FormField("promoCode")
    var orderCost by mutableStateOf(0.0)
    var visitDate: FormField = FormField("VisitDate").apply { value = LocalDate.now().toString() }
    var startTime: FormField = FormField(VisitDTO::reservationDate.name)
    var endTime: FormField = FormField(VisitDTO::endTime.name)
    var numberOfGuests by mutableStateOf(1)
    var tip by mutableStateOf(0.0)

    val addedItems = mutableStateListOf<Pair<RestaurantMenuItemDTO, Int>>()
    var participantIds: MutableList<String> = mutableListOf()

    var isTakeaway by mutableStateOf(false)
    var isDelivery by mutableStateOf(false)
    var deliveryAddress: FormField = FormField("deliveryAddress")
    var deliveryCost by mutableStateOf(0.0)

    // Pola stanu błędu
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

    // Flow na wyniki zapytań
    private val _orderResult = MutableStateFlow<Result<OrderDTO?>>(Result(isError = false, value = null))
    val orderResult: StateFlow<Result<OrderDTO?>> = _orderResult

    private val _visitResult = MutableStateFlow<Result<VisitDTO?>>(Result(isError = false, value = null))
    val visitResult: StateFlow<Result<VisitDTO?>> = _visitResult

    private val _deliveryResult = MutableStateFlow<Result<DeliveryDTO?>>(Result(isError = false, value = null))
    val deliveryResult: StateFlow<Result<DeliveryDTO?>> = _deliveryResult

    // Zaokrąglanie godzin "w górę" do najbliższej pełnej lub połówki
    private fun roundUpToNextHalfHour(time: LocalTime): LocalTime {
        val minute = time.minute
        return when {
            minute == 0 || minute == 30 -> time
            minute < 30 -> time.withMinute(30).withSecond(0).withNano(0)
            else -> time.plusHours(1).withMinute(0).withSecond(0).withNano(0)
        }
    }

    // Walidacja daty
    fun updateDate(dateString: String, restaurant: RestaurantDTO) {
        visitDate.value = dateString
        isDateError = false
        dateErrorText = null

        if (dateString.isEmpty()) {
            isDateError = true
            dateErrorText = "Nie wybrano daty"
            return
        }

        try {
            val selectedDate = LocalDate.parse(dateString)
            val today = LocalDate.now()

            if (selectedDate.isBefore(today)) {
                isDateError = true
                dateErrorText = "Data nie może być wcześniejsza niż dziś"
                return
            }

            val dayIndex = selectedDate.dayOfWeek.value - 1
            val dayHours = restaurant.openingHours?.getOrNull(dayIndex)

            if (dayHours?.from == null || dayHours.until == null) {
                isDateError = true
                dateErrorText = "Restauracja jest zamknięta w wybranym dniu"
            }

        } catch (e: Exception) {
            isDateError = true
            dateErrorText = "Nieprawidłowy format daty"
        }
    }

    // Walidacja godziny rozpoczęcia
    fun updateStartTime(timeString: String, restaurant: RestaurantDTO) {
        try {
            val inputTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
            val roundedTime = roundUpToNextHalfHour(inputTime)
            startTime.value = roundedTime.format(DateTimeFormatter.ofPattern("HH:mm"))

            val selectedDate = LocalDate.parse(visitDate.value)
            val now = LocalTime.now()
            if (selectedDate.isEqual(LocalDate.now()) && roundedTime.isBefore(now)) {
                isStartTimeError = true
                startTimeErrorText = "Godzina rozpoczęcia nie może być wcześniejsza niż aktualna"
                return
            }

            val dayIndex = selectedDate.dayOfWeek.value - 1
            val dayHours = restaurant.openingHours?.getOrNull(dayIndex)

            if (dayHours?.from == null || dayHours.until == null) {
                isStartTimeError = true
                startTimeErrorText = "Restauracja jest zamknięta w wybranym dniu"
                return
            }

            val ohFrom = LocalTime.parse(dayHours.from, DateTimeFormatter.ofPattern("HH:mm"))
            val ohUntil = LocalTime.parse(dayHours.until, DateTimeFormatter.ofPattern("HH:mm"))

            if (roundedTime.isBefore(ohFrom) || roundedTime.isAfter(ohUntil)) {
                isStartTimeError = true
                startTimeErrorText = "Godzina rozpoczęcia poza godzinami otwarcia"
            }

        } catch (e: Exception) {
            isStartTimeError = true
            startTimeErrorText = "Nieprawidłowy format godziny startu"
        }
    }

    // Walidacja godziny zakończenia
    fun updateEndTime(timeString: String, restaurant: RestaurantDTO, skipRounding: Boolean = false) {
        try {
            val inputTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
            val finalEndTime = if (skipRounding) inputTime else roundUpToNextHalfHour(inputTime)
            endTime.value = finalEndTime.format(DateTimeFormatter.ofPattern("HH:mm"))

            val selectedDate = LocalDate.parse(visitDate.value)
            val start = LocalTime.parse(startTime.value, DateTimeFormatter.ofPattern("HH:mm"))

            if (!finalEndTime.isAfter(start)) {
                isEndTimeError = true
                endTimeErrorText = "Czas zakończenia musi być późniejszy niż czas rozpoczęcia"
                return
            }

            val diffMinutes = java.time.Duration.between(start, finalEndTime).toMinutes()
            if (diffMinutes > 90) {
                isEndTimeError = true
                endTimeErrorText = "Rezerwacja nie może przekraczać 1.5 godziny"
                return
            }

            val dayIndex = selectedDate.dayOfWeek.value - 1
            val dayHours = restaurant.openingHours?.getOrNull(dayIndex)

            if (dayHours?.from == null || dayHours.until == null) {
                isEndTimeError = true
                endTimeErrorText = "Restauracja jest zamknięta w wybranym dniu"
                return
            }

            val ohFrom = LocalTime.parse(dayHours.from, DateTimeFormatter.ofPattern("HH:mm"))
            val ohUntil = LocalTime.parse(dayHours.until, DateTimeFormatter.ofPattern("HH:mm"))
            if (finalEndTime.isBefore(ohFrom) || finalEndTime.isAfter(ohUntil)) {
                isEndTimeError = true
                endTimeErrorText = "Godzina zakończenia poza godzinami otwarcia"
            }

        } catch (e: Exception) {
            isEndTimeError = true
            endTimeErrorText = "Nieprawidłowy format godziny zakończenia"
        }
    }

    // Sprawdzenie czy rezerwacja jest poprawna
    fun isReservationValid(): Boolean {
        return !(isDateError || isStartTimeError || isEndTimeError)
    }

    // Zarządzanie koszykiem
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

    // Tworzenie wizyty i zamówienia
    fun createVisitAndOrder(
        restaurantId: Int,
        isTakeaway: Boolean,
        isDelivery: Boolean
    ) {
        viewModelScope.launch {
            val visit = VisitDTO(
                date = "${visitDate.value}T${startTime.value}",
                endTime = "${visitDate.value}T${endTime.value}",
                numberOfGuests = numberOfGuests,
                tip = tip,
                takeaway = isTakeaway,
                restaurantId = restaurantId,
                participantIds = participantIds
            )
            val visitResult = visitsService.createVisit(visit)
            if (!visitResult.isError && visitResult.value != null) {
                val visitId = visitResult.value!!.visitId
                val orderItems = addedItems.map { (menuItem, quantity) ->
                    OrderDTO.OrderItemDTO(
                        menuItemId = menuItem.menuItemId,
                        amount = quantity
                    )
                }
                val order = OrderDTO(
                    visitId = visitId,
                    note = note.value,
                    items = orderItems
                )
                val orderResult = ordersService.createOrder(order)
                _orderResult.value = orderResult

                if (orderResult.isError) {
                    errorMessage = "Failed to place order. Please try again."
                } else {
                    addedItems.clear()
                    errorMessage = null
                }
            } else {
                _visitResult.value = visitResult
                errorMessage = "Failed to create visit. Please try again."
            }
        }
    }

    // Obsługa zamówień i dostaw
    fun createOrder() {
        viewModelScope.launch {
            val order = OrderDTO(
                cost = orderCost,
                note = note.value
            )
            val result = ordersService.createOrder(order)
            _orderResult.value = result
        }
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

    fun createVisit() {
        viewModelScope.launch {
            val visit = VisitDTO(
                reservationDate = visitDate.value,
                numberOfGuests = numberOfGuests
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
}
