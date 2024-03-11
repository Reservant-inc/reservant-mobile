package com.example.reservant_mobile.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO

class LoginViewModel : ViewModel() {

    private val isValid = MutableLiveData<Boolean>()

    fun validate(loginCredentialsDTO: LoginCredentialsDTO) {

        with(loginCredentialsDTO){
            isValid.value = login.isNotBlank() && password.isNotBlank()
        }

    }

}