package reservant_mobile.ui.viewmodels

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
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.FriendRequestDTO
import reservant_mobile.data.models.dtos.FriendStatus
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.models.dtos.UserSummaryDTO
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.EventService
import reservant_mobile.data.services.FriendsService
import reservant_mobile.data.services.IEventService
import reservant_mobile.data.services.IFriendsService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.IVisitsService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.services.UserService.UserObject
import reservant_mobile.data.services.VisitsService
import reservant_mobile.data.utils.GetUserEventsCategory

class ProfileViewModel(
    private val userService: IUserService = UserService(),
    private val eventService: IEventService = EventService(),
    private val friendsService: IFriendsService = FriendsService(),
    private val visitsService: IVisitsService = VisitsService(),
    private val profileUserId: String
) : ReservantViewModel() {
    var simpleProfileUser: UserSummaryDTO? by mutableStateOf(null)
    var fullProfileUser: UserDTO? by mutableStateOf(null)

    var isLoading: Boolean by mutableStateOf(false)
    var isCurrentUser: Boolean by mutableStateOf(false)
    var friendRequestError: String? by mutableStateOf(null)

    private val _friendsFlow = MutableStateFlow<Flow<PagingData<FriendRequestDTO>>?>(null)
    val friendsFlow: StateFlow<Flow<PagingData<FriendRequestDTO>>?> = _friendsFlow

    private val _eventsFlow = MutableStateFlow<Flow<PagingData<EventDTO>>?>(null)
    val eventsFlow: StateFlow<Flow<PagingData<EventDTO>>?> = _eventsFlow

    private val _ownedEventsFlow = MutableStateFlow<Flow<PagingData<EventDTO>>?>(null)
    val ownedEventsFlow: StateFlow<Flow<PagingData<EventDTO>>?> = _ownedEventsFlow

    private val _visitsFlow = MutableStateFlow<Flow<PagingData<VisitDTO>>?>(null)
    val visitsFlow: StateFlow<Flow<PagingData<VisitDTO>>?> = _visitsFlow

    private val _interestedUsersFlows = mutableMapOf<String, Flow<PagingData<EventDTO.Participant>>>()

    init {
        viewModelScope.launch {
            isLoading = true
            if (UserObject.userId == profileUserId) {
                isCurrentUser = true
                loadFullUser()
                fetchFriends()
                fetchUserEvents()
                fetchOwnedEvents()
                fetchUserVisits()
            }

            loadSimpleUser()
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

    private fun fetchFriends() {
        viewModelScope.launch {
            val result: Result<Flow<PagingData<FriendRequestDTO>>?> = friendsService.getFriends()

            if (!result.isError) {
                _friendsFlow.value = result.value?.cachedIn(viewModelScope)
            }
        }
    }

    private fun fetchUserVisits() {
        viewModelScope.launch {
            val result: Result<Flow<PagingData<VisitDTO>>?> = userService.getUserVisits()

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

    private fun fetchOwnedEvents() {
        viewModelScope.launch {
            val result: Result<Flow<PagingData<EventDTO>>?> = userService.getUserEvents(
                category = GetUserEventsCategory.CreatedBy
            )

            if (!result.isError) {
                _ownedEventsFlow.value = result.value?.cachedIn(viewModelScope)
            }
        }
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

    fun rejectFriendRequest() {
        viewModelScope.launch {
            simpleProfileUser?.userId?.let { userId ->
                val result = friendsService.deleteFriendOrRequest(userId)
                if (result.isError) {
                    friendRequestError = "Nie udało się odrzucić zaproszenia"
                } else {
                    simpleProfileUser = simpleProfileUser?.copy(friendStatus = FriendStatus.Stranger)
                    friendRequestError = null
                    fetchFriends()
                }
            }
        }
    }
}
