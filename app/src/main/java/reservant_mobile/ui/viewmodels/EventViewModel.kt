package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.services.EventService
import reservant_mobile.data.services.IEventService
import reservant_mobile.data.services.UserService

class EventViewModel(
    private val eventId: Int = 0,
    fetchRestaurants: Boolean = true, // true for AddEventActivity, false for EventDetailActivity
    private val eventService: IEventService = EventService(),
) : ReservantViewModel() {

    var isLoading: Boolean by mutableStateOf(false)

    var event by mutableStateOf<EventDTO?>(null)
    var isEventOwner by mutableStateOf(false)

    // Interested users as a paging flow
    private val _interestedUsersFlow = MutableStateFlow<Flow<PagingData<EventDTO.Participant>>?>(null)
    val interestedUsersFlow: StateFlow<Flow<PagingData<EventDTO.Participant>>?> = _interestedUsersFlow

    // Participants list
    var participants = mutableStateListOf<EventDTO.Participant>()
        private set

    init {
        viewModelScope.launch {
            getEvent()
        }
    }

    suspend fun acceptUser(userId: String) {
        val result = eventService.acceptUser(eventId, userId)
        if (!result.isError) {
            // Refresh participants list
            refreshParticipants()
            // Refresh interested users list
            refreshInterestedUsers()
        }
    }

    suspend fun rejectUser(userId: String) {
        val result = eventService.rejectUser(eventId, userId)
        if (!result.isError) {
            // Refresh interested users list
            refreshInterestedUsers()
        }
    }

    private suspend fun getEvent(): Boolean {
        isLoading = true

        val resultEvent = eventService.getEvent(eventId)
        isLoading = false

        if (resultEvent.isError) {
            return false
        }
        event = resultEvent.value
        isEventOwner = event?.creator?.userId == UserService.UserObject.userId

        // Initialize participants list
        refreshParticipants()

        // Fetch interested users if the user is the event owner
        if (isEventOwner) {
            refreshInterestedUsers()
        }

        return true
    }

    private fun refreshParticipants() {
        participants.clear()
        participants.addAll(event?.participants ?: emptyList())
    }

    private fun refreshInterestedUsers() {
        viewModelScope.launch {
            val res = eventService.getInterestedUser(eventId = eventId)
            if (!res.isError && res.value != null) {
                _interestedUsersFlow.value = res.value.cachedIn(viewModelScope)
            } else {
                _interestedUsersFlow.value = null
            }
        }
    }
}
