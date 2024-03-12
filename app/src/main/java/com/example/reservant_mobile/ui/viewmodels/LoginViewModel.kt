package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var login by mutableStateOf("")
    var password by mutableStateOf("")

    fun validateLogin(): Boolean {
        return !(isInvalidLogin(login) || isInvalidPassword(password))
    }

    private fun isInvalidLogin(login: String) : Boolean{
        return login.isBlank()
    }
    private fun isInvalidPassword(password: String) : Boolean{
        return password.isBlank()
    }

}