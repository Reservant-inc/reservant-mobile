package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.regex.Pattern
import com.example.reservant_mobile.ui.constants.*

data class FormState(
    val isValid: Boolean = false,
    val errorMessage: String
)

class RegisterViewModel : ViewModel() {

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var birthday by mutableStateOf("")
    var email by mutableStateOf("")
    var phoneNum by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    fun validateForm(): Boolean {
        return !(
                isInvalidWithRegex(NAME_REG, firstName) ||
                        isInvalidWithRegex(NAME_REG, lastName)  ||
                        isInvalidWithRegex(DATE_REG, birthday)  ||
                        isInvalidWithRegex(EMAIL_REG, email)    ||
                        isInvalidWithRegex(PHONE_REG, phoneNum) ||
                        isInvalidWithRegex(PASSWORD_REG, password) ||
                        confirmPassword != password
                )
    }

    private fun isInvalidWithRegex(regex: String, str: String): Boolean{
        return !Pattern.matches(regex, str)
    }
}