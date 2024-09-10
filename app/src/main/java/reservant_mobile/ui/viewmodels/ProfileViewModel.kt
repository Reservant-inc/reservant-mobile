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
import reservant_mobile.data.models.dtos.FriendRequestDTO
import reservant_mobile.data.models.dtos.LoggedUserDTO
import reservant_mobile.data.models.dtos.UserSummaryDTO
import reservant_mobile.data.services.FriendsService
import reservant_mobile.data.services.IFriendsService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.models.dtos.fields.Result

class ProfileViewModel(
    private val userService: IUserService = UserService(),
    private val friendsService: IFriendsService = FriendsService(),
    private val profileUserId: String
) : ViewModel() {
    var user: LoggedUserDTO? by mutableStateOf(null)
    var profileUser: UserSummaryDTO? by mutableStateOf(null)

    var isLoading: Boolean by mutableStateOf(false)
    var isCurrentUser: Boolean by mutableStateOf(true)
    var isFriend: Boolean by mutableStateOf(false)
    var isRequestSent: Boolean by mutableStateOf(false)
    var friendRequestError: String? by mutableStateOf(null)

    private val _friendsFlow = MutableStateFlow<Flow<PagingData<FriendRequestDTO>>?>(null)
    val friendsFlow: StateFlow<Flow<PagingData<FriendRequestDTO>>?> = _friendsFlow

    init {
        viewModelScope.launch {
            loadUser()
            if (user?.userId != profileUserId) {
                isCurrentUser = false
                loadUser(userId = profileUserId)
            }
            if (!isCurrentUser) {
                checkFriendshipStatus()
                fetchFriends()
            }
        }
    }

    private suspend fun loadUser(): Boolean {
        isLoading = true
        val resultUser = userService.getUser()
        if (resultUser.isError) {
            isLoading = false
            return false
        }
        user = resultUser.value
        isLoading = false
        return true
    }

    private suspend fun loadUser(userId: String): Boolean {
        isLoading = true
        val resultUser = userService.getUserSimpleInfo(userId)
        if (resultUser.isError) {
            isLoading = false
            return false
        }
        profileUser = resultUser.value
        isLoading = false
        return true
    }

    private suspend fun checkFriendshipStatus() {
        val friendsResult = friendsService.getFriends()
        if (!friendsResult.isError) {
            friendsResult.value?.collect { pagingData ->
                isFriend = pagingData.toString().contains(profileUserId)
            }
        }

        if (!isFriend) {
            val outgoingRequestsResult = friendsService.getOutgoingFriendRequests()
            if (!outgoingRequestsResult.isError) {
                outgoingRequestsResult.value?.collect { pagingData ->
                    isRequestSent = pagingData.toString().contains(profileUserId)
                }
            }
        }
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

    fun sendFriendRequest() {
        viewModelScope.launch {
            profileUser?.userId?.let { userId ->
                val result = friendsService.sendFriendRequest(userId)
                if (result.isError) {
                    friendRequestError = "Nie udało się wysłać zaproszenia"
                } else {
                    isRequestSent = true
                    friendRequestError = null
                    fetchFriends()
                }
            }
        }
    }

    fun cancelFriendRequest() {
        viewModelScope.launch {
            profileUser?.userId?.let { userId ->
                val result = friendsService.deleteFriendOrRequest(userId)
                if (result.isError) {
                    friendRequestError = "Nie udało się anulować zaproszenia"
                } else {
                    isRequestSent = false
                    friendRequestError = null
                    fetchFriends()
                }
            }
        }
    }

    fun removeFriend() {
        viewModelScope.launch {
            profileUser?.userId?.let { userId ->
                val result = friendsService.deleteFriendOrRequest(userId)
                if (result.isError) {
                    friendRequestError = "Nie udało się usunąć znajomego"
                } else {
                    isFriend = false
                    friendRequestError = null
                    fetchFriends()
                }
            }
        }
    }
}