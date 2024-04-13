package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.data.services.IUserService
import com.example.reservant_mobile.data.services.UserService

class LoginViewModel(private val userService: IUserService = UserService()) : ViewModel() {



    //var login by mutableStateOf("")
    var login: FormField = FormField(LoginCredentialsDTO::login.name)
    var password by mutableStateOf("")

    suspend fun login(): Int{

        if (isInvalidLogin()){
            return R.string.error_login_wrong_credentials
        }

        return userService.loginUser(
            LoginCredentialsDTO(
                login = login.value,
                password = password,
                rememberMe = true
            )
        )
    }

    private fun isInvalidLogin(): Boolean {
        return isInvalid(login.value) || isInvalid(password)
    }

    private fun isInvalid(str: String) : Boolean{
        return str.isBlank()
    }
}