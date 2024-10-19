// AddEventViewModel.kt
package reservant_mobile.ui.viewmodels

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

    fun addEvent(event: EventDTO) {
        isSaving.value = true
        viewModelScope.launch {
            val result = eventService.addEvent(event)
            this@AddEventViewModel.result = result
            isSaving.value = false
        }
    }
}
