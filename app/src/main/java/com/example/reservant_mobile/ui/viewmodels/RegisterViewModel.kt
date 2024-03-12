package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import java.util.regex.Pattern

data class FormState(
    val isValid: Boolean = false,
    val errorMessage: String
)

class RegisterViewModel : ViewModel() {

    private val registerFormState = MutableLiveData<FormState>()

    private val firstName by mutableStateOf("")
    private val lastName by mutableStateOf("")
    private val birthday by mutableStateOf("")
    private val email by mutableStateOf("")
    private val phoneNum by mutableStateOf("")
    private val password by mutableStateOf("")
    private val confirmPassword by mutableStateOf("")

    private val dateRegex = "\\d{4}-\\d{2}-\\d{2}"
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    private val phoneRegex = "^\\d{9}$"

    fun validateForm(): Boolean {
        return !(
                firstName.isBlank() ||
                lastName.isBlank()  ||
                isInvalidWithRegex(dateRegex, birthday)  ||
                isInvalidWithRegex(emailRegex, email)    ||
                isInvalidWithRegex(phoneRegex, phoneNum) ||
                password.isBlank() ||
                confirmPassword.isBlank()
        )
    }

    private fun isInvalidWithRegex(regex: String, str: String): Boolean{
        return !Pattern.matches(regex, str)
    }
}