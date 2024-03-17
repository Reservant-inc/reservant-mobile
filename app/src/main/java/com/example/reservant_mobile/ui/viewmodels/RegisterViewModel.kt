package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import java.util.regex.Pattern

data class FormState(
    val isValid: Boolean = false,
    val errorMessage: String
)

class RegisterViewModel : ViewModel() {

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var dayOfBirth by mutableStateOf("1")
    var monthOfBirth by mutableStateOf("1")
    var yearOfBirth by mutableStateOf("1999")
    var email by mutableStateOf("")
    var prefix by mutableStateOf("")
    var number by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    val birthday = "$yearOfBirth-$monthOfBirth-$dayOfBirth"
    val phoneNum = prefix+number
    val dateRegex = "\\d{4}-\\d{2}-\\d{2}"
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    val phoneRegex = "^\\d{9}$"

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