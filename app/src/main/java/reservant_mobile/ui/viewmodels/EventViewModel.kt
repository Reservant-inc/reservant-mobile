package reservant_mobile.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.reservant_mobile.R
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.FileUploadDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.DataType
import reservant_mobile.data.services.EventService
import reservant_mobile.data.services.IEventService
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.getFileFromUri
import reservant_mobile.data.utils.getFileName
import reservant_mobile.data.utils.isFileNameInvalid
import reservant_mobile.data.utils.isFileSizeInvalid
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventViewModel(
    private val eventId: Int = 0,
    fetchRestaurants: Boolean = true, // true for AddEventActivity, false for EventDetailActivity
    private val eventService: IEventService = EventService(),
    private val restaurantService: IRestaurantService = RestaurantService(),
    private val userService: IUserService = UserService()
) : ReservantViewModel() {

    var isLoading: Boolean by mutableStateOf(false)

    var event by mutableStateOf<EventDTO?>(null)
    var isEventOwner by mutableStateOf(false)

    // Interested users as a paging flow
    private val _interestedUsersFlow = MutableStateFlow<Flow<PagingData<EventDTO.Participant>>?>(null)
    val interestedUsersFlow: StateFlow<Flow<PagingData<EventDTO.Participant>>?> = _interestedUsersFlow

    var participants = mutableStateListOf<EventDTO.Participant>()
        private set

    private val currentDateTime = LocalDateTime.now()
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    private val _restaurantsFlow = MutableStateFlow<Flow<PagingData<RestaurantDTO>>?>(null)
    val restaurantsFlow: StateFlow<Flow<PagingData<RestaurantDTO>>?> = _restaurantsFlow

    val searchQuery = MutableStateFlow("")

    var eventName by mutableStateOf("")
    var description by mutableStateOf("")
    var eventDate by mutableStateOf("")
    var eventTime: String by mutableStateOf(currentDateTime.format(timeFormatter))
    var mustJoinDate by mutableStateOf("")
    var mustJoinTime: String by mutableStateOf(currentDateTime.format(timeFormatter))
    var maxPeople by mutableStateOf("")
    var photo by mutableStateOf("")
    var selectedRestaurant by mutableStateOf<RestaurantDTO?>(null)
    var formSent by mutableStateOf(false)
    var isInterested by mutableStateOf(false)


    var result by mutableStateOf(Result(isError = false, value = null as EventDTO?))

    var isSaving = mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            if(fetchRestaurants){
                fetchRestaurants()
            } else{
                getEvent()
                checkInterest()
            }
        }
    }

    suspend fun joinInterested(): Boolean {
        val result = eventService.markEventAsInterested(eventId)
        if (!result.isError) {
            return true
        }
        return false
    }

    suspend fun checkInterest(): Boolean {
        val result = userService.isInterestedInEvent(eventId)
        if (!result.isError) {
            when(result.value){
                true -> isInterested = true
                false -> isInterested = false
                null -> null
            }
            return true
        }
        return false
    }

    suspend fun markEventAsUnInterested(): Boolean {
        val result = eventService.markEventAsNotInterested(eventId)
        if (!result.isError) {
            return true
        }
        return false
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

    suspend fun deleteEvent(): Boolean {
        val result = eventService.deleteEvent(eventId)
        if (!result.isError) {
            return true
        }
        return false
    }

    suspend fun updateEvent(dto: EventDTO, context: Context): Boolean {
        if (dto.photo.isNullOrBlank()) {

            val oldPhoto = event?.photo ?: ""

            val filenameWithoutPrefix = oldPhoto.replace("/uploads/", "")

            val updatedDto = dto.copy(
                photo = filenameWithoutPrefix
            )

            val result = eventService.updateEvent(eventId, updatedDto)
            return if (!result.isError) {
                getEvent()
                true
            } else {
                println("ERROR WHILE UPDATING EVENT: ${result.errors}")
                false
            }
        } else {

            val eventPhotoResult = sendPhoto(dto.photo, context)
            if (eventPhotoResult != null && !eventPhotoResult.isError) {
                val newPhotoName = eventPhotoResult.value?.fileName ?: ""
                val updatedDto = dto.copy(
                    photo = newPhotoName
                )
                val result = eventService.updateEvent(eventId, updatedDto)
                if (!result.isError) {
                    getEvent()
                    return true
                } else {
                    println("ERROR WHILE UPDATING EVENT: ${result.errors}")
                }
            }
        }
        return false
    }


    private suspend fun getEvent(): Boolean {
        isLoading = true

        val resultEvent = eventService.getEvent(eventId)

        if (resultEvent.isError) {
            return false
        }
        event = resultEvent.value
        isEventOwner = event?.creator?.userId == UserService.UserObject.userId

        refreshParticipants()

        if(isEventOwner)
            refreshInterestedUsers()

        isLoading = false

        return true
    }

    private fun refreshParticipants() {

        val filteredParticipants = event?.participants?.filter {
            it.isArchived != true
        } ?: emptyList()

        participants.clear()
        participants.addAll(filteredParticipants)
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

    private suspend fun fetchRestaurants() {
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

    suspend fun addEvent(context: Context) {
        isSaving.value = true
        if (!isFormInvalid()) {
            val maxPeopleInt = maxPeople.toIntOrNull()
            val time = "${eventDate}T${eventTime}"
            val mustJoinUntil = "${mustJoinDate}T${mustJoinTime}"

            val eventPhotoResult = if (
                !photo.endsWith(".png", ignoreCase = true) &&
                !photo.endsWith(".jpg", ignoreCase = true) &&
                !isFileSizeInvalid(context, photo)
            ) {
                sendPhoto(photo, context)
            } else {
                null
            }

            if (eventPhotoResult != null) {
                if (!eventPhotoResult.isError) {
                    photo = eventPhotoResult.value?.fileName ?: ""
                } else {
                    // Handle error from photo upload
                    result = eventPhotoResult as Result<EventDTO?>
                    isSaving.value = false
                    return
                }
            }

            val newEvent = EventDTO(
                name = eventName,
                description = description,
                time = time,
                mustJoinUntil = mustJoinUntil,
                maxPeople = maxPeopleInt,
                restaurantId = selectedRestaurant?.restaurantId,
                photo = photo
            )

            val serviceResult = eventService.addEvent(newEvent)
            result = serviceResult
        } else {
            result = Result(isError = true, value = null)
        }
        isSaving.value = false
    }

    suspend fun getPhoto(photoStr: String): Bitmap? {
        val result = fileService.getImage(photoStr)
        if (!result.isError){
            return result.value!!
        }
        return null
    }

    private suspend fun sendPhoto(uri: String?, context: Context): Result<FileUploadDTO?>? {
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

    fun isEventNameInvalid(): Boolean {
        return eventName.isBlank() || eventName.length > 50
    }

    fun getEventNameError(): Int {
        return if (eventName.isBlank()) R.string.error_field_required else -1
    }

    fun isDescriptionInvalid(): Boolean {
        return description.isBlank() || description.length > 200 || getFieldError(result, "description") != -1
    }

    fun getDescriptionError(): Int {
        return when {
            description.isBlank() -> R.string.error_field_required
            description.length > 200 -> R.string.error_description_too_long
            else -> getFieldError(result, "description")
        }
    }

    fun getNameError(): Int {
        return when {
            description.isBlank() -> R.string.error_field_required
            description.length > 50 -> R.string.error_name_too_long
            else -> getFieldError(result, "name")
        }
    }

    fun isEventDateInvalid(): Boolean {
        return eventDate.isBlank() || getFieldError(result, "time") != -1
    }

    fun getEventTimeInvalid(): Boolean {
        return eventTime.isBlank()
    }

    fun isMustJoinDateInvalid(): Boolean {
        return mustJoinDate.isBlank() || getFieldError(result, "mustJoinUntil") != -1
    }

    fun getMustJoinDateError(): Int {
        return getFieldError(result, "mustJoinUntil")
    }

    fun getEventDateError(): Int {
        return getFieldError(result, "time")
    }

    fun isPhotoInvalid(): Boolean {
        return photo.isBlank() ||
                photo == "" ||
                getFieldError(result, "photo") != -1
    }

    fun getPhotoError(): Int {
        return getFieldError(result, "photo")
    }

    fun isMaxPeopleInvalid(): Boolean {
        val maxPeopleInt = maxPeople.toIntOrNull()
        return maxPeopleInt == null
    }

    fun getMaxPeopleError(): Int {
        return if (maxPeople.toIntOrNull() == null) R.string.error_invalid_number else -1
    }

    fun isSelectedRestaurantInvalid(): Boolean {
        return selectedRestaurant == null
    }

    fun getSelectedRestaurantError(): Int {
        return if (selectedRestaurant == null) R.string.error_select_restaurant else -1
    }


    fun isFormInvalid(): Boolean {
        return isEventNameInvalid() ||
                isDescriptionInvalid() ||
                isEventDateInvalid() ||
                isMustJoinDateInvalid() ||
                isPhotoInvalid() ||
                isMaxPeopleInvalid() ||
                isSelectedRestaurantInvalid()
    }

    fun getToastError(): Int {
        return getToastError(result)
    }
}