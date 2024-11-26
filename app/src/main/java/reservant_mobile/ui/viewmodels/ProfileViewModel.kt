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
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.models.dtos.UserSummaryDTO
import reservant_mobile.data.models.dtos.UserSummaryDTO.FriendStatus
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.FriendsService
import reservant_mobile.data.services.IFriendsService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.services.UserService.UserObject

class ProfileViewModel(
    private val userService: IUserService = UserService(),
    private val friendsService: IFriendsService = FriendsService(),
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

    init {
        viewModelScope.launch {
            isLoading = true
            if (UserObject.userId == profileUserId) {
                isCurrentUser = true
                loadFullUser()
                println("LOADED USER: $fullProfileUser")
            }

            loadSimpleUser()
            fetchFriends()
            fetchUserEvents()
            isLoading = false
        }
    }
    
    private suspend fun loadSimpleUser(): Boolean {

        val resultUser = userService.getUserSimpleInfo(profileUserId)
        if (resultUser.isError) {
            isLoading = false
            return false
        }
        simpleProfileUser = resultUser.value

        return true
    }

    private suspend fun loadFullUser(): Boolean {
        isLoading = true
        val resultUser = userService.getUserInfo()
        if (resultUser.isError) {
            isLoading = false
            return false
        }
        fullProfileUser = resultUser.value
        isLoading = false
        return true
    }

    private fun fetchFriends() {
        viewModelScope.launch {
            val result: Result<Flow<PagingData<FriendRequestDTO>>?> = friendsService.getFriends()

            if (!result.isError) {
                _friendsFlow.value = result.value?.cachedIn(viewModelScope)
            } else {
                val errors = result.errors
            }
        }
    }


    private fun fetchUserEvents() {
        viewModelScope.launch {
            isLoading = true
            val result: Result<Flow<PagingData<EventDTO>>?> = userService.getUserEvents()

            if (!result.isError) {
                _eventsFlow.value = result.value?.cachedIn(viewModelScope)
            } else {
                // Obsługa błędów
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
