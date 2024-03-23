package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO
import com.example.reservant_mobile.data.services.IUserService
import com.example.reservant_mobile.data.services.UserService
import kotlinx.coroutines.launch

class LoginViewModel(private val userService: IUserService = UserService()) : ViewModel() {

    var login by mutableStateOf("")
    var password by mutableStateOf("")
    private var code by mutableIntStateOf(0);

    suspend fun login(): Boolean{

        return if(validateLogin()){
            // TODO: loading screen, 3 different responses instead true/false
            code = userService.loginUser(
                LoginCredentialsDTO(
                    login = login,
                    password = password,
                    rememberMe = true
                )
            ) as Int
            when(code){
                200 -> true
                else -> false
            }
        }else{
            false;
        }
    }

    private fun validateLogin(): Boolean {
        return !(isInvalid(login) || isInvalid(password))
    }

    private fun isInvalid(str: String) : Boolean{
        return str.isBlank()
    }
}