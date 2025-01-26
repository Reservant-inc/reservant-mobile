package reservant_mobile.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.Regex
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.FriendRequestDTO
import reservant_mobile.data.models.dtos.FriendStatus
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.ThreadDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.models.dtos.UserSummaryDTO
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.EventService
import reservant_mobile.data.services.FriendsService
import reservant_mobile.data.services.IEventService
import reservant_mobile.data.services.IFriendsService
import reservant_mobile.data.services.IThreadsService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.IVisitsService
import reservant_mobile.data.services.ThreadsService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.services.UserService.UserObject
import reservant_mobile.data.services.VisitsService
import reservant_mobile.data.utils.GetUserEventsCategory

class ProfileViewModel(
    private val userService: IUserService = UserService(),
    private val eventService: IEventService = EventService(),
    private val friendsService: IFriendsService = FriendsService(),
    private val visitsService: IVisitsService = VisitsService(),
    private val threadsService: IThreadsService = ThreadsService(),
    private val profileUserId: String
) : ReservantViewModel() {

    var simpleProfileUser: UserSummaryDTO? by mutableStateOf(null)
    var fullProfileUser: UserDTO? by mutableStateOf(null)

    var isLoading: Boolean by mutableStateOf(false)
    var isCurrentUser: Boolean by mutableStateOf(false)
    var friendRequestError: String? by mutableStateOf(null)

    private val _friendsFlow = MutableStateFlow<Flow<PagingData<FriendRequestDTO>>?>(null)
    val friendsFlow: StateFlow<Flow<PagingData<FriendRequestDTO>>?> = _friendsFlow

    private val _friendsRequestsFlow = MutableStateFlow<Flow<PagingData<FriendRequestDTO>>?>(null)
    val friendsRequestsFlow: StateFlow<Flow<PagingData<FriendRequestDTO>>?> = _friendsRequestsFlow

    private val _eventsFlow = MutableStateFlow<Flow<PagingData<EventDTO>>?>(null)
    val eventsFlow: StateFlow<Flow<PagingData<EventDTO>>?> = _eventsFlow

    private val _ownedEventsFlow = MutableStateFlow<Flow<PagingData<EventDTO>>?>(null)
    val ownedEventsFlow: StateFlow<Flow<PagingData<EventDTO>>?> = _ownedEventsFlow

    private val _visitsFlow = MutableStateFlow<Flow<PagingData<VisitDTO>>?>(null)
    val visitsFlow: StateFlow<Flow<PagingData<VisitDTO>>?> = _visitsFlow

    private val _interestedUsersFlows = mutableMapOf<String, Flow<PagingData<EventDTO.Participant>>>()

    private var updateProfileResult by mutableStateOf<Result<UserDTO?>?>(null)

    private val _userThreadsFlow = MutableStateFlow<Flow<PagingData<ThreadDTO>>?>(null)

    val oldPassword: FormField = FormField("oldPassword")
    val newPassword: FormField = FormField("newPassword")
    val repeatNewPassword: FormField = FormField("none") ///idfc
    private var changePasswordResult by mutableStateOf(Result(isError=false, value=false))


    init {
        viewModelScope.launch {
            isLoading = true
            if (UserObject.userId == profileUserId) {
                isCurrentUser = true
                loadFullUser()
                fetchFriends()
                fetchFriendRequests()
                fetchUserEvents()
                fetchEvents()
                fetchUserVisits()
            }

            loadSimpleUser()
            loadUserThreads()
            isLoading = false
        }
    }

    private suspend fun loadSimpleUser(): Boolean {
        val resultUser = userService.getUserSimpleInfo(profileUserId)
        if (resultUser.isError) {
            return false
        }
        simpleProfileUser = resultUser.value
        return true
    }

    private suspend fun loadFullUser(): Boolean {
        val resultUser = userService.getUserInfo()
        if (resultUser.isError) {
            return false
        }
        fullProfileUser = resultUser.value
        return true
    }

    private suspend fun fetchFriends() {
        val result = friendsService.getFriends()
        if (!result.isError) {
            _friendsFlow.value = result.value?.cachedIn(viewModelScope)
        }
    }

    private fun fetchFriendRequests() {
        viewModelScope.launch {
            val result = friendsService.getIncomingFriendRequests()
            if (!result.isError) {
                _friendsRequestsFlow.value = result.value?.cachedIn(viewModelScope)
            }
        }
    }

    private fun fetchUserVisits() {
        viewModelScope.launch {
            val result = userService.getUserVisits()
            if (!result.isError) {
                _visitsFlow.value = result.value?.cachedIn(viewModelScope)
            }
        }
    }

    fun confirmArrival(visitId: String) {
        viewModelScope.launch {
            val result = visitsService.confirmStart(visitId)
            if (!result.isError) {
                fetchUserVisits()
            }
        }
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            val result = userService.getUserEvents(
                category = GetUserEventsCategory.All
            )

            if (!result.isError) {
                _ownedEventsFlow.value = result.value?.cachedIn(viewModelScope)
            }
        }
    }

    fun updateProfile(user: UserDTO) {
        if(user.phoneNumber != null && isPhoneInvalid(user.phoneNumber.number)){
            return
        }

        viewModelScope.launch {
            val result = userService.editUserInfo(user)
            updateProfileResult = result
            if (!result.isError) {
                loadFullUser()
            }
        }
    }

    suspend fun changePassword(): Boolean {
        if(newPassword.value != repeatNewPassword.value) return false

        changePasswordResult = userService.changePassword(oldPassword.value, newPassword.value)

        return !changePasswordResult.isError &&
                !isNewPasswordInvalid() &&
                getFieldError(changePasswordResult,newPassword.name) == -1 &&
                getFieldError(changePasswordResult,oldPassword.name) == -1
    }

    fun isPhoneInvalid(phoneNum: String): Boolean {
        return isInvalidWithRegex(Regex.PHONE_REG, phoneNum)
    }

    fun getInterestedUsersFlow(eventId: String): Flow<PagingData<EventDTO.Participant>>? {
        return _interestedUsersFlows[eventId]
    }

    fun fetchInterestedUsers(eventId: String) {
        if (_interestedUsersFlows.containsKey(eventId)) return

        viewModelScope.launch {
            val result = eventService.getInterestedUser(eventId)
            if (!result.isError) {
                val flow = result.value?.cachedIn(viewModelScope)
                if (flow != null) {
                    _interestedUsersFlows[eventId] = flow
                }
            }
        }
    }

    fun acceptUser(eventId: String, userId: String) {
        viewModelScope.launch {
            val result = eventService.acceptUser(eventId, userId)
            if (!result.isError) {
                fetchInterestedUsers(eventId)
            }
        }
    }

    fun rejectUser(eventId: String, userId: String) {
        viewModelScope.launch {
            val result = eventService.rejectUser(eventId, userId)
            if (!result.isError) {
                fetchInterestedUsers(eventId)
            }
        }
    }

    suspend fun getPhoto(photoStr: String): Bitmap? {
        val result = fileService.getImage(photoStr)
        if (!result.isError){
            return result.value!!
        }
        return null
    }

    private fun fetchUserEvents() {
        viewModelScope.launch {
            isLoading = true
            val result: Result<Flow<PagingData<EventDTO>>?> = userService.getUserEvents()

            if (!result.isError) {
                _eventsFlow.value = result.value?.cachedIn(viewModelScope)
            }
            isLoading = false
        }
    }

    fun sendFriendRequest() {
        viewModelScope.launch {
            simpleProfileUser?.userId?.let { userId ->
                val result = friendsService.sendFriendRequest(userId)
                if (result.isError) {
                    friendRequestError = "Nie udało się wysłać zaproszenia"
                } else {
                    simpleProfileUser = simpleProfileUser?.copy(friendStatus = FriendStatus.OutgoingRequest)
                    friendRequestError = null
                    fetchFriends()
                }
            }
        }
    }

    fun cancelFriendRequest() {
        viewModelScope.launch {
            simpleProfileUser?.userId?.let { userId ->
                val result = friendsService.deleteFriendOrRequest(userId)
                if (result.isError) {
                    friendRequestError = "Nie udało się anulować zaproszenia"
                } else {
                    simpleProfileUser = simpleProfileUser?.copy(friendStatus = FriendStatus.Stranger)
                    friendRequestError = null
                    fetchFriends()
                }
            }
        }
    }

    fun removeFriend() {
        viewModelScope.launch {
            simpleProfileUser?.userId?.let { userId ->
                val result = friendsService.deleteFriendOrRequest(userId)
                if (result.isError) {
                    friendRequestError = "Nie udało się usunąć znajomego"
                } else {
                    simpleProfileUser = simpleProfileUser?.copy(friendStatus = FriendStatus.Stranger)
                    friendRequestError = null
                    fetchFriends()
                }
            }
        }
    }

    fun acceptFriendRequest(userId: String) {
        viewModelScope.launch {
            val result = friendsService.acceptFriendRequest(userId)
            if (result.isError) {
                friendRequestError = "Nie udało się zaakceptować zaproszenia"
            } else {
                simpleProfileUser = simpleProfileUser?.copy(friendStatus = FriendStatus.Friend)
                friendRequestError = null
                fetchFriendRequests()
                fetchFriends()
            }
        }
    }

    fun acceptFriendRequest() {
        viewModelScope.launch {
            simpleProfileUser?.userId?.let { userId ->
                val result = friendsService.acceptFriendRequest(userId)
                if (result.isError) {
                    friendRequestError = "Nie udało się zaakceptować zaproszenia"
                } else {
                    simpleProfileUser = simpleProfileUser?.copy(friendStatus = FriendStatus.Friend)
                    friendRequestError = null
                    fetchFriends()
                }
            }
        }
    }

    fun rejectFriendRequest(userId: String) {
        viewModelScope.launch {
            val result = friendsService.deleteFriendOrRequest(userId)
            if (result.isError) {
                friendRequestError = "Nie udało się odrzucić zaproszenia"
            } else {
                simpleProfileUser = simpleProfileUser?.copy(friendStatus = FriendStatus.Stranger)
                friendRequestError = null
                fetchFriendRequests()
                fetchFriends()
            }
        }
    }

    fun rejectFriendRequest() {
        viewModelScope.launch {
            simpleProfileUser?.userId?.let { userId ->
                val result = friendsService.deleteFriendOrRequest(userId)
                if (result.isError) {
                    friendRequestError = "Nie udało się usunąć znajomego"
                } else {
                    simpleProfileUser = simpleProfileUser?.copy(friendStatus = FriendStatus.Stranger)
                    friendRequestError = null
                    fetchFriends()
                }
            }
        }
    }

    private fun loadUserThreads() {
        viewModelScope.launch {
            val result = userService.getUserThreads()
            if (!result.isError && result.value != null) {
                _userThreadsFlow.value = result.value.cachedIn(viewModelScope)
            } else {
                // error
            }
        }
    }

    fun getUserThreads(): Flow<PagingData<ThreadDTO>> {
        return _userThreadsFlow.value ?: kotlinx.coroutines.flow.flow {
            emit(PagingData.empty())
        }
    }

    fun createThreadWithUser(targetUserId: String, onResult: (ThreadDTO?) -> Unit) {
        viewModelScope.launch {
            val result = threadsService.createThread(
                title = "New Chat",
                participantIds = listOf(targetUserId)
            )
            if (!result.isError && result.value != null) {
                onResult(result.value)
            } else {
                onResult(null)
            }
        }
    }

    fun getOldPasswordError(): Int {
        val res = getFieldError(changePasswordResult,oldPassword.name)
        return res
    }

    fun getNewPasswordError(): Int {
        return getFieldError(changePasswordResult,newPassword.name)
    }

    fun isNewPasswordInvalid() : Boolean{
        return isInvalidWithRegex(Regex.PASSWORD_REG, newPassword.value) ||
                getFieldError(changePasswordResult, newPassword.name) != -1
    }


}
