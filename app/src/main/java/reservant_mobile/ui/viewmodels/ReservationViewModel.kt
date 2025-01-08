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

    var visitDate: FormField = FormField("VisitDate")
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
        visitDate.value = dateString

        // Reset błędów
        isDateError = false
        dateErrorText = null

        // Sprawdzamy, czy w ogóle wybrano datę
        if (dateString.isEmpty()) {
            isDateError = true
            dateErrorText = "Nie wybrano daty"
            return
        }

        try {
            val selectedDate = LocalDate.parse(dateString) // Format yyyy-MM-dd
            val today = LocalDate.now()

            // (1) Data nie może być wcześniejsza niż dzisiaj
            if (selectedDate.isBefore(today)) {
                isDateError = true
                dateErrorText = "Data nie może być wcześniejsza niż dziś"
            }

            // (2) Sprawdzenie godzin otwarcia - czy restauracja w ogóle jest czynna w ten dzień
            val dayIndex = selectedDate.dayOfWeek.value - 1 // Monday->0, Tuesday->1, ...
            // Jeżeli dayIndex poza zakresem lub restaurant.openingHours jest null/za krótkie
            if (dayIndex !in 0..6 || restaurant.openingHours.isNullOrEmpty() || restaurant.openingHours.size < 7) {
                isDateError = true
                dateErrorText = "Brak danych o godzinach otwarcia"
                return
            }

            val dayHours = restaurant.openingHours[dayIndex]
            // dayHours będzie np. AvailableHours(from="09:00:00", until="17:00:00")
            if (dayHours.from == null || dayHours.until == null) {
                // Zamknięte w ten dzień
                isDateError = true
                dateErrorText = "Restauracja jest zamknięta w wybranym dniu"
            }

        } catch (e: Exception) {
            isDateError = true
            dateErrorText = "Nieprawidłowy format daty"
        }
    }

    // ------------------
    // WALIDACJA GODZINY STARTU
    // ------------------
    fun updateStartTime(timeString: String, restaurant: RestaurantDTO) {

        // Najpierw usuwamy ewentualne stare błędy
        isStartTimeError = false
        startTimeErrorText = null

        // Jeżeli pole jest puste
        if (timeString.isEmpty()) {
            isStartTimeError = true
            startTimeErrorText = "Nie wybrano godziny startu"
            startTime.value = ""
            return
        }

        val dateStr = visitDate.value
        if (dateStr.isEmpty()) {
            // Jeśli użytkownik najpierw wpisał godzinę bez wybrania daty,
            // to nie mamy do czego porównać -> błąd lub wymuszenie wybrania daty wcześniej
            isStartTimeError = true
            startTimeErrorText = "Najpierw wybierz datę"
            startTime.value = ""
            return
        }

        try {
            // Parsujemy godzinę
            val inputTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
            // Zaokrąglamy w górę do pełnej lub połówki
            val roundedTime = roundUpToNextHalfHour(inputTime)

            // Ustawiamy w polu startTime już po zaokrągleniu, żeby UI mogło to odzwierciedlić
            startTime.value = roundedTime.format(DateTimeFormatter.ofPattern("HH:mm"))

            // Sprawdzenie czy data to dzisiaj
            val selectedDate = LocalDate.parse(dateStr)
            val today = LocalDate.now()
            val now = LocalTime.now()
            val isToday = selectedDate.isEqual(today)

            // (1) Jeśli to dzisiaj, to start nie może być w przeszłości
            if (isToday && roundedTime.isBefore(now)) {
                isStartTimeError = true
                startTimeErrorText = "Godzina rozpoczęcia nie może być wcześniejsza niż aktualna"
            }

            // (2) Sprawdzenie godzin otwarcia restauracji
            val dayIndex = selectedDate.dayOfWeek.value - 1
            val dayHours = restaurant.openingHours?.getOrNull(dayIndex)
            if (dayHours?.from == null || dayHours.until == null) {
                // Zamknięte w ten dzień
                isStartTimeError = true
                startTimeErrorText = "Restauracja jest zamknięta w wybranym dniu"
            } else {
                val ohFrom = LocalTime.parse(dayHours.from, DateTimeFormatter.ofPattern("HH:mm:ss"))
                val ohUntil = LocalTime.parse(dayHours.until, DateTimeFormatter.ofPattern("HH:mm:ss"))

                if (roundedTime.isBefore(ohFrom) || roundedTime.isAfter(ohUntil)) {
                    isStartTimeError = true
                    startTimeErrorText = "Godzina rozpoczęcia poza godzinami otwarcia"
                }
            }

            // Jeśli nie ma błędów w starcie, to automatycznie ustawiamy endTime na +1h.
            // (Jeśli mamy błąd, nie ma sensu ustawiać endTime)
            if (!isStartTimeError) {
                val proposedEnd = roundedTime.plusHours(1)
                val newEndStr = proposedEnd.format(DateTimeFormatter.ofPattern("HH:mm"))
                updateEndTime(newEndStr, restaurant, skipRounding = true)
            }

        } catch (e: Exception) {
            isStartTimeError = true
            startTimeErrorText = "Nieprawidłowy format godziny startu"
            startTime.value = ""
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

        // Najpierw usuwamy ewentualne stare błędy
        isEndTimeError = false
        endTimeErrorText = null

        // Jeżeli pole jest puste
        if (timeString.isEmpty()) {
            isEndTimeError = true
            endTimeErrorText = "Nie wybrano godziny zakończenia"
            endTime.value = ""
            return
        }

        val dateStr = visitDate.value
        if (dateStr.isEmpty()) {
            isEndTimeError = true
            endTimeErrorText = "Najpierw wybierz datę"
            endTime.value = ""
            return
        }

        val startStr = startTime.value
        if (startStr.isEmpty()) {
            // Jeśli ktoś ustawia endTime, ale startTime nie jest jeszcze ustawiony (albo jest błędny)
            isEndTimeError = true
            endTimeErrorText = "Najpierw ustaw godzinę rozpoczęcia"
            endTime.value = ""
            return
        }

        try {
            // Parsujemy "podaną" godzinę zakończenia
            val inputTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))

            // Możliwe, że w środku wywołania chcemy pominąć rounding (np. user właśnie wprowadza),
            // ale w większości przypadków chcemy zaokrąglić tak samo jak startTime, „w górę”.
            val finalEndTime = if (skipRounding) {
                inputTime
            } else {
                roundUpToNextHalfHour(inputTime)
            }

            // Przepisujemy do stanu (z formatowaniem HH:mm)
            endTime.value = finalEndTime.format(DateTimeFormatter.ofPattern("HH:mm"))

            // Sprawdzenie logiki. Musi być:
            // (1) koniec > start
            // (2) max 1.5 godz. różnicy
            // (3) w godzinach otwarcia

            val selectedDate = LocalDate.parse(dateStr)
            val st = LocalTime.parse(startStr, DateTimeFormatter.ofPattern("HH:mm"))

            // (1) end > start
            if (!finalEndTime.isAfter(st)) {
                isEndTimeError = true
                endTimeErrorText = "Czas zakończenia musi być późniejszy niż czas rozpoczęcia"
                return
            }

            // (2) max 1.5 godziny różnicy
            val diffMinutes = java.time.Duration.between(st, finalEndTime).toMinutes()
            if (diffMinutes > 90) {
                isEndTimeError = true
                endTimeErrorText = "Rezerwacja nie może przekraczać 1.5 godziny (90 minut)"
                return
            }

            // (3) w godzinach otwarcia
            val dayIndex = selectedDate.dayOfWeek.value - 1
            val dayHours = restaurant.openingHours?.getOrNull(dayIndex)
            if (dayHours?.from == null || dayHours.until == null) {
                isEndTimeError = true
                endTimeErrorText = "Restauracja jest zamknięta w wybranym dniu"
                return
            } else {
                val ohFrom = LocalTime.parse(dayHours.from, DateTimeFormatter.ofPattern("HH:mm:ss"))
                val ohUntil = LocalTime.parse(dayHours.until, DateTimeFormatter.ofPattern("HH:mm:ss"))

                if (finalEndTime.isBefore(ohFrom) || finalEndTime.isAfter(ohUntil)) {
                    isEndTimeError = true
                    endTimeErrorText = "Godzina zakończenia poza godzinami otwarcia"
                }
            }

        } catch (e: Exception) {
            isEndTimeError = true
            endTimeErrorText = "Nieprawidłowy format godziny zakończenia"
            endTime.value = ""
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
