package com.example.reservant_mobile.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO

class LoginViewModel : ViewModel() {

    private val isValid = MutableLiveData<Boolean>()

    fun validate(loginCredentialsDTO: LoginCredentialsDTO): Boolean {
        val result: Boolean = validateLogin(loginCredentialsDTO.login) && validatePassword(loginCredentialsDTO.password)
        isValid.value = result
        return result
    }

    private fun validateLogin(login: String) : Boolean{
        return login.isNotBlank()
    }
    private fun validatePassword(password: String) : Boolean{
        return password.isNotBlank()
    }

}