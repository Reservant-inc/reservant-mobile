package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.LoggedUserDTO
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.UserService

class ProfileViewModel(
    private val userService: IUserService = UserService()
) : ReservantViewModel() {

    var user: LoggedUserDTO? by mutableStateOf(null)
    var isLoading: Boolean by mutableStateOf(false)
    var isCurrentUser: Boolean by mutableStateOf(false)

    init {
        viewModelScope.launch {
            loadUser()
        }
    }

    private suspend fun loadUser(): Boolean {
        isLoading = true
        val resultUser =  userService.getUser()
        if(resultUser.isError){
            return false
        }
        user = resultUser.value
        isLoading = false
        return true
    }

}