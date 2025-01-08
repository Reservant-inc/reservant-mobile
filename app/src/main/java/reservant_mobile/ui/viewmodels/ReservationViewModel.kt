package reservant_mobile.ui.viewmodels

import android.util.Log
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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class ReservationViewModel(
    private val ordersService: IOrdersService = OrdersService(),
    private val visitsService: IVisitsService = VisitsService(),
    private val deliveryService: IDeliveryService = DeliveryService()
) : ReservantViewModel() {

    // ------------------
    // Pola powiązane z formularzem
    // ------------------

    var note: FormField = FormField(OrderDTO::note.name)
    var promoCode: FormField = FormField("promoCode") // np. pole na kod promocyjny
    var orderCost by mutableStateOf(0.0)

    var visitDate: FormField = FormField("VisitDate").apply {
        value = LocalDate.now().toString() // Ustawienie domyślnej wartości na dzisiejszą datę
    }
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

    // ------------------
    // Pola stanu błędu
    // ------------------

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Błędy daty
    var isDateError by mutableStateOf(false)
        private set
    var dateErrorText by mutableStateOf<String?>(null)
        private set

    // Błędy godziny startu
    var isStartTimeError by mutableStateOf(false)
        private set
    var startTimeErrorText by mutableStateOf<String?>(null)
        private set

    // Błędy godziny zakończenia
    var isEndTimeError by mutableStateOf(false)
        private set
    var endTimeErrorText by mutableStateOf<String?>(null)
        private set

    // ------------------
    // Flow na wyniki zapytań
    // ------------------

    private val _orderResult = MutableStateFlow<Result<OrderDTO?>>(Result(isError = false, value = null))
    val orderResult: StateFlow<Result<OrderDTO?>> = _orderResult

    private val _visitResult = MutableStateFlow<Result<VisitDTO?>>(Result(isError = false, value = null))
    val visitResult: StateFlow<Result<VisitDTO?>> = _visitResult

    private val _deliveryResult = MutableStateFlow<Result<DeliveryDTO?>>(Result(isError = false, value = null))
    val deliveryResult: StateFlow<Result<DeliveryDTO?>> = _deliveryResult


    // ------------------
    // Pomocnicza funkcja do zaokrąglania godzin "w górę" do najbliższej pełnej lub połówki
    // ------------------
    private fun roundUpToNextHalfHour(time: LocalTime): LocalTime {
        val minute = time.minute
        return when {
            // Jeśli minuty to dokładnie :00 lub :30 -> nic nie zmieniamy
            minute == 0 || minute == 30 -> time
            // Jeśli minute < 30 -> zaokrąglamy do :30
            minute < 30 -> time.withMinute(30).withSecond(0).withNano(0)
            // W przeciwnym wypadku do kolejnej pełnej godziny
            else -> time.plusHours(1)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
        }
    }

    // ------------------
    // WALIDACJA DATY
    // ------------------
    fun updateDate(dateString: String, restaurant: RestaurantDTO) {
        Log.d("DEBUG", "updateDate() called with dateString=$dateString")

        visitDate.value = dateString
        isDateError = false
        dateErrorText = null

        if (dateString.isEmpty()) {
            isDateError = true
            dateErrorText = "Nie wybrano daty"
            Log.d("DEBUG", "Date is empty")
            return
        }

        try {
            val selectedDate = LocalDate.parse(dateString)
            val today = LocalDate.now()
            Log.d("DEBUG", "Parsed selectedDate=$selectedDate, today=$today")

            if (selectedDate.isBefore(today)) {
                isDateError = true
                dateErrorText = "Data nie może być wcześniejsza niż dziś"
                Log.d("DEBUG", "Selected date is before today")
                return
            }

            val dayIndex = selectedDate.dayOfWeek.value - 1
            val dayHours = restaurant.openingHours?.getOrNull(dayIndex)
            Log.d("DEBUG", "Opening hours for dayIndex=$dayIndex: $dayHours")

            if (dayHours?.from == null || dayHours.until == null) {
                isDateError = true
                dateErrorText = "Restauracja jest zamknięta w wybranym dniu"
                Log.d("DEBUG", "Restaurant is closed on this day")
                return
            }

        } catch (e: Exception) {
            isDateError = true
            dateErrorText = "Nieprawidłowy format daty"
            Log.e("DEBUG", "Failed to parse date: ${e.message}")
        }
    }


    // ------------------
    // WALIDACJA GODZINY STARTU
    // ------------------
    fun updateStartTime(timeString: String, restaurant: RestaurantDTO) {
        Log.d("DEBUG", "updateStartTime() called with timeString=$timeString, visitDate=${visitDate.value}")

        try {
            val inputTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
            Log.d("DEBUG", "Parsed time: $inputTime")

            val roundedTime = roundUpToNextHalfHour(inputTime)
            startTime.value = roundedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            Log.d("DEBUG", "Rounded time: $roundedTime")

            // Sprawdzenie daty
            val selectedDate = LocalDate.parse(visitDate.value)
            Log.d("DEBUG", "Selected date: $selectedDate")

            val now = LocalTime.now()
            if (selectedDate.isEqual(LocalDate.now()) && roundedTime.isBefore(now)) {
                isStartTimeError = true
                startTimeErrorText = "Godzina rozpoczęcia nie może być wcześniejsza niż aktualna"
                Log.d("DEBUG", "Start time is in the past")
                return
            }

            // Sprawdzenie godzin otwarcia
            val dayIndex = selectedDate.dayOfWeek.value - 1
            val dayHours = restaurant.openingHours?.getOrNull(dayIndex)
            Log.d("DEBUG", "Opening hours for dayIndex=$dayIndex: $dayHours")

            if (dayHours?.from == null || dayHours.until == null) {
                isStartTimeError = true
                startTimeErrorText = "Restauracja jest zamknięta w wybranym dniu"
                Log.d("DEBUG", "Restaurant is closed on this day")
                return
            }

            val ohFrom = LocalTime.parse(dayHours.from, DateTimeFormatter.ofPattern("HH:mm"))
            val ohUntil = LocalTime.parse(dayHours.until, DateTimeFormatter.ofPattern("HH:mm"))
            Log.d("DEBUG", "Opening hours: from=$ohFrom, until=$ohUntil")

            if (roundedTime.isBefore(ohFrom) || roundedTime.isAfter(ohUntil)) {
                isStartTimeError = true
                startTimeErrorText = "Godzina rozpoczęcia poza godzinami otwarcia"
                Log.d("DEBUG", "Start time is outside opening hours")
            }

        } catch (e: Exception) {
            isStartTimeError = true
            startTimeErrorText = "Nieprawidłowy format godziny startu"
            Log.e("DEBUG", "Failed to parse startTime: ${e.message}")
        }
    }


    // ------------------
    // WALIDACJA GODZINY ZAKOŃCZENIA
    // ------------------
    /**
     * [skipRounding] – jeżeli chcemy wywołać `updateEndTime` wewnętrznie (np. po setStartTime)
     *   z już obliczoną godziną. Wtedy nie wykonujemy ponownie rounding-u.
     */
    fun updateEndTime(timeString: String, restaurant: RestaurantDTO, skipRounding: Boolean = false) {
        Log.d("DEBUG", "updateEndTime() called with timeString=$timeString, visitDate=${visitDate.value}, startTime=${startTime.value}")

        try {
            val inputTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
            val finalEndTime = if (skipRounding) inputTime else roundUpToNextHalfHour(inputTime)
            endTime.value = finalEndTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            Log.d("DEBUG", "Rounded endTime: $finalEndTime")

            val selectedDate = LocalDate.parse(visitDate.value)
            val start = LocalTime.parse(startTime.value, DateTimeFormatter.ofPattern("HH:mm"))
            Log.d("DEBUG", "Parsed startTime: $start")

            if (!finalEndTime.isAfter(start)) {
                isEndTimeError = true
                endTimeErrorText = "Czas zakończenia musi być późniejszy niż czas rozpoczęcia"
                Log.d("DEBUG", "End time is not after start time")
                return
            }

            val diffMinutes = java.time.Duration.between(start, finalEndTime).toMinutes()
            if (diffMinutes > 90) {
                isEndTimeError = true
                endTimeErrorText = "Rezerwacja nie może przekraczać 1.5 godziny"
                Log.d("DEBUG", "Reservation duration exceeds 1.5 hours")
                return
            }

            val dayIndex = selectedDate.dayOfWeek.value - 1
            val dayHours = restaurant.openingHours?.getOrNull(dayIndex)
            Log.d("DEBUG", "Opening hours for dayIndex=$dayIndex: $dayHours")

            if (dayHours?.from == null || dayHours.until == null) {
                isEndTimeError = true
                endTimeErrorText = "Restauracja jest zamknięta w wybranym dniu"
                Log.d("DEBUG", "Restaurant is closed on this day")
                return
            }

            val ohFrom = LocalTime.parse(dayHours.from, DateTimeFormatter.ofPattern("HH:mm"))
            val ohUntil = LocalTime.parse(dayHours.until, DateTimeFormatter.ofPattern("HH:mm"))
            if (finalEndTime.isBefore(ohFrom) || finalEndTime.isAfter(ohUntil)) {
                isEndTimeError = true
                endTimeErrorText = "Godzina zakończenia poza godzinami otwarcia"
                Log.d("DEBUG", "End time is outside opening hours")
            }

        } catch (e: Exception) {
            isEndTimeError = true
            endTimeErrorText = "Nieprawidłowy format godziny zakończenia"
            Log.e("DEBUG", "Failed to parse endTime: ${e.message}")
        }
    }


    // ------------------
    // SPRAWDZENIE CZY REZERWACJA (DATA + GODZINY) JEST POPRAWNA
    // ------------------
    fun isReservationValid(): Boolean {
        // Jeśli którykolwiek z tych błędów jest aktywny, nie można iść dalej
        return !(isDateError || isStartTimeError || isEndTimeError)
    }

    // ------------------
    // Zarządzanie koszykiem
    // ------------------
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

    // ------------------
    // Tworzenie wizyt i zamówień
    // ------------------
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
                // Now create order
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
                    // Clear the cart and reset error message
                    addedItems.clear()
                    errorMessage = null
                }
            } else {
                // Handle error
                _visitResult.value = visitResult
                errorMessage = "Failed to create visit. Please try again."
            }
        }
    }

    // Przykładowe metody do obsługi zamówień/odwołań/dostaw – do ewentualnego wykorzystania
    fun createOrder() {
        viewModelScope.launch {
            val order = OrderDTO(
                cost = orderCost,
                note = note.value
                // Inne pola w razie potrzeby
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
                numberOfGuests = numberOfGuests,
                // ...
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
                cost = deliveryCost,
                // ...
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
