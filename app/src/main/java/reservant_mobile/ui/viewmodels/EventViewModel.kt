package reservant_mobile.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.FileUploadDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.DataType
import reservant_mobile.data.services.EventService
import reservant_mobile.data.services.IEventService
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.getFileFromUri
import reservant_mobile.data.utils.getFileName
import reservant_mobile.data.utils.isFileNameInvalid

class EventViewModel(
    private val eventId: Int = 0,
    fetchRestaurants: Boolean = true, // true for AddEventActivity, false for EventDetailActivity
    private val eventService: IEventService = EventService(),
    private val restaurantService: IRestaurantService = RestaurantService(),
) : ReservantViewModel() {

    private val _restaurantsFlow = MutableStateFlow<Flow<PagingData<RestaurantDTO>>?>(null)
    val restaurantsFlow: StateFlow<Flow<PagingData<RestaurantDTO>>?> = _restaurantsFlow

    private val _interestedState = MutableStateFlow<PagingData<UserDTO>>(PagingData.empty())
    val interested: StateFlow<PagingData<UserDTO>> = _interestedState.asStateFlow()

    val searchQuery = MutableStateFlow("")
    var isLoading: Boolean by mutableStateOf(false)

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

    var event by mutableStateOf<EventDTO?>(null)
    var isEventOwner by mutableStateOf(false)
    var eventInterested by mutableStateOf<List<UserDTO?>?>(null)

    var result: Result<EventDTO?> = Result(isError = false, value = null)
        private set

    var isSaving = mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            if(fetchRestaurants)
                fetchRestaurants()
            else
                getEvent()
        }
    }

    suspend fun acceptUser(userId: String){
        eventService.acceptUser(eventId, userId)
    }

    suspend fun rejectUser(userId: String){
        eventService.rejectUser(eventId, userId)
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

    private suspend fun getEvent(): Boolean{
        isLoading = true

        val resultEvent = eventService.getEvent(eventId)
        isLoading = false

        if (resultEvent.isError) {
            return false
        }
        event = resultEvent.value
        isEventOwner = event!!.creator!!.userId == UserService.UserObject.userId

        // fetching interested list
        if(isEventOwner){
            val res = eventService.getInterestedUser(
                eventId = eventId
            )
            if(res.isError || res.value == null)
                throw Exception()

            res.value.cachedIn(viewModelScope).collect { pagingData ->
                _interestedState.value = pagingData.map { dto ->
                    UserDTO(
                        userId = dto.userId,
                        firstName = dto.firstName,
                        lastName = dto.lastName,
                        photo = dto.photo // TODO: NULL
                    )
                }
            }

        }

        return true
    }

    suspend fun updateEvent() {
        // post
    }

    suspend fun addEvent(context: Context) {
        isSaving.value = true
        if (isFormValid()) {
            val maxPeopleInt = maxPeople.toIntOrNull()
            val time = "${eventDate}T${eventTime}"
            val mustJoinUntil = "${mustJoinDate}T${mustJoinTime}"

            sendPhoto(photo, context)

            val newEvent = EventDTO(
                name = eventName,
                description = description,
                time = time,
                mustJoinUntil = mustJoinUntil,
                maxPeople = maxPeopleInt,
                restaurantId = selectedRestaurant?.restaurantId,
                photo = photo
            )
            viewModelScope.launch {
                val result = eventService.addEvent(newEvent)
                this@EventViewModel.result = result
                isSaving.value = false
            }
        } else {
            result = Result(isError = true, value = null)
            isSaving.value = false
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