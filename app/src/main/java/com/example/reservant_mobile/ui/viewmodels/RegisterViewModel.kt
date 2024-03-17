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
    var birthday by mutableStateOf("")
    var email by mutableStateOf("")
    var phoneNum by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    private val nameRegex = "^[A-Za-z]+$"
    private val dateRegex = "\\d{4}-\\d{2}-\\d{2}"
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"

    // Format - od 9 do 15 cyfr, opcjonalnie zaczynając od +
    private val phoneRegex = "^\\+?\\d{9,15}$"

    // Min 8 znaków, co najmniej jedna litera mała, jedna wielka, jedna cyfra i jeden znak specjalny
    private val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"

    fun validateForm(): Boolean {
        return !(
                isInvalidWithRegex(nameRegex, firstName) ||
                        isInvalidWithRegex(nameRegex, lastName)  ||
                        isInvalidWithRegex(dateRegex, birthday)  ||
                        isInvalidWithRegex(emailRegex, email)    ||
                        isInvalidWithRegex(phoneRegex, phoneNum) ||
                        isInvalidWithRegex(passwordRegex, password) ||
                        confirmPassword != password
                )
    }

    private fun isInvalidWithRegex(regex: String, str: String): Boolean{
        return !Pattern.matches(regex, str)
    }
}