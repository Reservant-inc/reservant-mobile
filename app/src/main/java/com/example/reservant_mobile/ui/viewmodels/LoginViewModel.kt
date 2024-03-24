package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO
import com.example.reservant_mobile.data.services.IUserService
import com.example.reservant_mobile.data.services.UserService
import kotlinx.coroutines.launch

class LoginViewModel(private val userService: IUserService = UserService()) : ViewModel() {

    var login by mutableStateOf("")
    var password by mutableStateOf("")
    private var code by mutableIntStateOf(0);

    suspend fun login(): Int{

        return if(validateLogin()){
            userService.loginUser(
                LoginCredentialsDTO(
                    login = login,
                    password = password,
                    rememberMe = true
                )
            )

        }else{
            R.string.error_login_wrong_credentials
        }
    }

    private fun validateLogin(): Boolean {
        return !(isInvalid(login) || isInvalid(password))
    }

    private fun isInvalid(str: String) : Boolean{
        return str.isBlank()
    }
}