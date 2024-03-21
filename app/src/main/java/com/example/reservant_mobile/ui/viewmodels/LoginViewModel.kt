package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO
import com.example.reservant_mobile.data.services.IUserService
import kotlinx.coroutines.launch

class LoginViewModel(private val userService: IUserService) : ViewModel() {

    var login by mutableStateOf("")
    var password by mutableStateOf("")
    private var code by mutableIntStateOf(0);

    fun login(): Boolean{

        if(validateLogin()){
            viewModelScope.launch {
                code = userService.loginUser(
                    LoginCredentialsDTO(
                        email = login,
                        password = password,
                        rememberMe = true
                    )
                ) as Int
            }
            return when(code){
                200 -> true
                else -> false
            }
        }else{
            return false;
        }
    }

    private fun validateLogin(): Boolean {
        return !(isInvalid(login) || isInvalid(password))
    }

    private fun isInvalid(str: String) : Boolean{
        return str.isBlank()
    }
}