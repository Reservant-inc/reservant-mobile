package com.example.reservant_mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.example.reservant_mobile.data.models.dtos.RegisterUserDTO
import java.util.regex.Pattern

data class FormState(
    val isValid: Boolean = false,
    val errorMessages: List<String> = emptyList()
)

class RegisterViewModel : ViewModel() {

    private val registerFormState = MutableLiveData<FormState>()

    fun validateForm(registerUserDTO: RegisterUserDTO): Boolean {
        val errors = mutableListOf<String>()

        with(registerUserDTO) {

            if (firstName.isBlank()) errors.add("First name cannot be empty")
            if (lastName.isBlank()) errors.add("Last name cannot be empty")
            validateWithRegex(birthday, email, phoneNum, errors)

            registerFormState.value = FormState(isValid = errors.isEmpty(), errorMessages = errors)
            return errors.isEmpty()
        }
    }

    private fun validateWithRegex(birthday: String, email: String, phoneNum: String, errors: MutableList<String>){

        val dateRegex = "\\d{4}-\\d{2}-\\d{2}"
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        val phoneRegex = "^\\d{9}$"

        if (!Pattern.matches(dateRegex, birthday))
            errors.add("Birthday is not in the correct format")

        if (!Pattern.matches(emailRegex, email))
            errors.add("Email is not in the correct format")

        if (!Pattern.matches(phoneRegex, phoneNum))
            errors.add("Phone number is not in the correct format")
    }



}
