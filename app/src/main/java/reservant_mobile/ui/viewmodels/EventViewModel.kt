package reservant_mobile.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.*
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.FileUploadDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.DataType
import reservant_mobile.data.services.EventService
import reservant_mobile.data.services.IEventService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.getFileFromUri
import reservant_mobile.data.utils.getFileName
import reservant_mobile.data.utils.isFileNameInvalid
import reservant_mobile.data.utils.isFileSizeInvalid

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
            refreshParticipants()
            refreshInterestedUsers()
        }
    }

    suspend fun rejectUser(userId: String) {
        val result = eventService.rejectUser(eventId, userId)
        if (!result.isError) {
            refreshInterestedUsers()
        }
    }

    suspend fun updateEvent(dto: EventDTO, context: Context){

        val eventPhotoResult = if (
            !dto.photo!!.endsWith(".png", ignoreCase = true) &&
            !dto.photo.endsWith(".jpg", ignoreCase = true) &&
            !isFileSizeInvalid(context, dto.photo)
        ) {
            sendPhoto(dto.photo, context)
        } else {
            null
        }

        val resultDTO: EventDTO

        if (eventPhotoResult != null) {
            if (!eventPhotoResult.isError) {
                resultDTO = EventDTO(
                    eventId = eventId,
                    name = dto.name,
                    description = dto.description,
                    maxPeople = dto.maxPeople,
                    restaurantId = dto.restaurantId,
                    time = dto.time,
                    mustJoinUntil = dto.mustJoinUntil,
                    photo = eventPhotoResult.value?.fileName ?: ""
                )
                val result = eventService.updateEvent(eventId, resultDTO)
                if(!result.isError){
                    getEvent()
                }
            }
        }else{
            // error
        }
    }

    suspend fun sendPhoto(uri: String?, context: Context): Result<FileUploadDTO?>? {
        if (isFileNameInvalid(uri?.let { getFileName(context, it) })) {
            return null
        }

        val file = uri?.let { getFileFromUri(context, it.toUri()) }
        var fDto = file?.let { fileService.sendFile(DataType.PNG, it) }
        if (fDto != null) {
            if (fDto.value == null) {
                fDto = file?.let { fileService.sendFile(DataType.JPG, it) }
            }
        }
        return fDto
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

        refreshParticipants()

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
