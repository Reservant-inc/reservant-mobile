package reservant_mobile.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.FoundUserDTO
import reservant_mobile.data.models.dtos.ThreadDTO
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.UserService

class SocialViewModel(
    private val userService: IUserService = UserService()
): ReservantViewModel() {

    private val _users = MutableStateFlow<PagingData<FoundUserDTO>>(PagingData.empty())
    val users: StateFlow<PagingData<FoundUserDTO>> = _users.asStateFlow()
    private val _threads = MutableStateFlow<PagingData<ThreadDTO>>(PagingData.empty())
    var threads: StateFlow<PagingData<ThreadDTO>> = _threads.asStateFlow()

    var threadQuery = ""
    var userQuery = mutableStateOf("")

    init {
        viewModelScope.launch {
            getThreads(threadQuery)
            getUsers(userQuery.value)
        }
    }

    suspend fun getPhoto(url: String): Bitmap?{
        val result = fileService.getImage(url)
        if (!result.isError){
            return result.value!!
        }
        return null
    }

    suspend fun getUsers(query: String){
        val res = userService.getUsers(name = query)

        if (res.isError || res.value == null){
            throw Exception()
        }

        res.value.cachedIn(viewModelScope).collect{
            _users.value = it
        }
    }

    suspend fun getThreads(query: String) {
        val res = userService.getUserThreads()

        if (res.isError || res.value == null){
            throw Exception()
        }

        res.value.cachedIn(viewModelScope).collect {
            _threads.value = it.filter { thread ->
                thread.title?.contains(query) ?: false ||
                thread.participants?.any { participant ->
                    participant.firstName.contains(query)
                } ?: false
            }
        }
    }

}