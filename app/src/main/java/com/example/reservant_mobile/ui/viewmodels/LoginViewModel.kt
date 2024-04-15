package com.example.reservant_mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.data.services.IUserService
import com.example.reservant_mobile.data.services.UserService
import com.example.reservant_mobile.data.models.dtos.fields.Result

class LoginViewModel(private val userService: IUserService = UserService()) : ViewModel() {

    var result: Result<Boolean> = Result(isError = false, value = false)
    var login: FormField = FormField(LoginCredentialsDTO::login.name)
    var password: FormField = FormField(LoginCredentialsDTO::password.name)

    suspend fun login(): Boolean{

        if (isFormInvalid()){
            return false
        }

        result = userService.loginUser(
            LoginCredentialsDTO(
                login = login.value,
                password = password.value,
                rememberMe = true
            )
        )

        return result.value
    }

    suspend fun refreshToken(): Boolean{
        return userService.refreshToken()
    }
    
    private fun isLoginInvalid(): Boolean{
        return isInvalid(login.value) ||
                getFieldError(login.name) != -1
    }

    private fun isPasswordInvalid(): Boolean{
        return isInvalid(password.value) ||
                getFieldError(password.name) != -1
    }

    private fun isFormInvalid(): Boolean {
        return isLoginInvalid() || isPasswordInvalid()
    }

    private fun isInvalid(str: String) : Boolean{
        return str.isBlank()
    }

    private fun getFieldError(name: String): Int{
        if(!result.isError){
            return -1
        }

        return result.errors!!.getOrDefault(name, -1)
    }

    fun getToastError(): Int{
        return getFieldError("TOAST")
    }
}