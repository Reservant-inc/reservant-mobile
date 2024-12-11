package reservant_mobile.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.FoundUserDTO
import reservant_mobile.data.models.dtos.ThreadDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.UserService

class SocialViewModel(
    private val userService: IUserService = UserService()
): ReservantViewModel() {

    private val _users = MutableStateFlow<PagingData<UserDTO>>(PagingData.empty())
    val users: StateFlow<PagingData<UserDTO>> = _users.asStateFlow()
    private val _threads = MutableStateFlow<PagingData<ThreadDTO>>(PagingData.empty())
    var threads: StateFlow<PagingData<ThreadDTO>> = _threads.asStateFlow()

    var userQuery = mutableStateOf("")

    init {
        viewModelScope.launch {
            getThreads()

        }
        getUsers()
    }

    suspend fun getPhoto(url: String): Bitmap?{
        val result = fileService.getImage(url)
        if (!result.isError){
            return result.value!!
        }
        return null
    }

    fun getUsers(){
        viewModelScope.launch {
            val res = userService.getUsers(name = userQuery.value)
            if (!res.isError && res.value != null){
                res.value.cachedIn(viewModelScope).collect{
                    _users.value = it
                }
            }
        }
    }

    suspend fun getThreads() {
        val res = userService.getUserThreads()

        if (!res.isError && res.value != null){
            res.value.cachedIn(viewModelScope).collect {
                _threads.value = it
            }
        }

    }

}