package com.example.reservant_mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.example.reservant_mobile.data.models.dtos.RegisterUserDTO
import java.util.regex.Pattern

data class FormState(
    val isValid: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel : ViewModel() {

    private val registerFormState = MutableLiveData<FormState>()

    fun validateForm(registerUserDTO: RegisterUserDTO) {
        with(registerUserDTO) {
            when {
                firstName.isBlank() -> registerFormState.value = FormState(errorMessage = "First name cannot be empty")
                lastName.isBlank() -> registerFormState.value = FormState(errorMessage = "Last name cannot be empty")
                !isDateValid(birthday) -> registerFormState.value = FormState(errorMessage = "Birthday is not in the correct format")
                !isEmailValid(email) -> registerFormState.value = FormState(errorMessage = "Email is not in the correct format")
                !isPhoneNumberValid(phoneNum) -> registerFormState.value = FormState(errorMessage = "Phone number is not in the correct format")
                else -> registerFormState.value = FormState(isValid = true)
            }
        }
    }

    private fun isDateValid(date: String): Boolean {
        // Dla formatu daty rrrr-mm-dd
        val regex = "\\d{4}-\\d{2}-\\d{2}"
        return Pattern.matches(regex, date)
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return Pattern.matches(emailRegex, email)
    }

    private fun isPhoneNumberValid(phone: String): Boolean {
        val phoneRegex = "^\\d{9}$";
        return Pattern.matches(phoneRegex, phone)
    }
}
