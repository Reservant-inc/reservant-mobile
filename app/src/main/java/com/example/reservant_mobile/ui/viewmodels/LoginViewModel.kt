package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val login by mutableStateOf("")
    private val password by mutableStateOf("")

    fun validate(): Boolean {
        return validateLogin(login) && validatePassword(password)
    }

    private fun validateLogin(login: String) : Boolean{
        return login.isNotBlank()
    }
    private fun validatePassword(password: String) : Boolean{
        return password.isNotBlank()
    }

}