package reservant_mobile.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.FoundUserDTO
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.UserService

class SocialViewModel(
    private val userService: IUserService = UserService()
): ReservantViewModel() {

    private val _users = MutableStateFlow<PagingData<FoundUserDTO>>(PagingData.empty())
    val users: StateFlow<PagingData<FoundUserDTO>> = _users.asStateFlow()

    init {
        viewModelScope.launch {
            getUsers("")
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
    

}