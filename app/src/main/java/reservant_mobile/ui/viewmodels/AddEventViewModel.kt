package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.EventService
import reservant_mobile.data.services.IEventService
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.RestaurantService

class AddEventViewModel(
    private val eventService: IEventService = EventService(),
    private val restaurantService: IRestaurantService = RestaurantService()
) : ViewModel() {

    private val _restaurantsFlow = MutableStateFlow<Flow<PagingData<RestaurantDTO>>?>(null)
    val restaurantsFlow: StateFlow<Flow<PagingData<RestaurantDTO>>?> = _restaurantsFlow

    val searchQuery = MutableStateFlow("")

    var eventName by  mutableStateOf("")
    var description by mutableStateOf("")
    var eventDate by mutableStateOf("")
    var eventTime by mutableStateOf("")
    var mustJoinDate by mutableStateOf("")
    var mustJoinTime by mutableStateOf("")
    var maxPeople by mutableStateOf("")
    var photo by mutableStateOf("")
    var selectedRestaurant by mutableStateOf<RestaurantDTO?>(null)
    var formSent by mutableStateOf(false)

    var result: Result<EventDTO?> = Result(isError = false, value = null)
        private set

    var isSaving = mutableStateOf(false)
        private set

    init {
        fetchRestaurants()
    }

    private fun fetchRestaurants() {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    val result = restaurantService.getRestaurants(name = if (query.isBlank()) null else query)
                    if (!result.isError) {
                        _restaurantsFlow.value = result.value?.cachedIn(viewModelScope)
                    }
                }
        }
    }

    fun addEvent() {
        isSaving.value = true
        if (isFormValid()) {
            val maxPeopleInt = maxPeople.toIntOrNull()
            val time = "${eventDate}T${eventTime}"
            val mustJoinUntil = "${mustJoinDate}T${mustJoinTime}"

            val newEvent = EventDTO(
                name = eventName,
                description = description,
                time = time,
                mustJoinUntil = mustJoinUntil,
                maxPeople = maxPeopleInt!!,
                restaurantId = selectedRestaurant?.restaurantId
             )
            viewModelScope.launch {
                val result = eventService.addEvent(newEvent)
                this@AddEventViewModel.result = result
                isSaving.value = false
            }
        } else {
            result = Result(isError = true, value = null)
            isSaving.value = false
        }
    }

    fun isFormValid(): Boolean {
        val maxPeopleInt = maxPeople.toIntOrNull()
        return eventName.isNotBlank() &&
                description.isNotBlank() &&
                eventDate.isNotBlank() &&
                eventTime.isNotBlank() &&
                mustJoinDate.isNotBlank() &&
                mustJoinTime.isNotBlank() &&
                photo.isNotBlank() &&
                maxPeopleInt != null
    }
}
