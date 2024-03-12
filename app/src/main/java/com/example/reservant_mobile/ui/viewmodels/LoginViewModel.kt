package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var login by mutableStateOf("")
    var password by mutableStateOf("")

    fun validateLogin(): Boolean {
        return !(isInvalid(login) || isInvalid(password))
    }

    private fun isInvalid(str: String) : Boolean{
        return str.isBlank()
    }
}