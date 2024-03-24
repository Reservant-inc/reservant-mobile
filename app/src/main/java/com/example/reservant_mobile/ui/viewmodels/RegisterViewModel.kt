package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RegisterUserDTO
import com.example.reservant_mobile.data.services.IUserService
import com.example.reservant_mobile.data.services.UserService
import com.example.reservant_mobile.ui.constants.DATE_REG
import com.example.reservant_mobile.ui.constants.EMAIL_REG
import com.example.reservant_mobile.ui.constants.NAME_REG
import com.example.reservant_mobile.ui.constants.PASSWORD_REG
import com.example.reservant_mobile.ui.constants.PHONE_REG
import com.example.reservant_mobile.data.utils.getCountriesList
import java.util.regex.Pattern

class RegisterViewModel(private val userService: IUserService = UserService()) : ViewModel() {

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var birthday by mutableStateOf("")
    var phoneNum by mutableStateOf("")
    val countriesList = getCountriesList()
    var mobileCountry by mutableStateOf(getCountriesList().firstOrNull { it.nameCode == "pl" })

    suspend fun register() : Int{

        if (isRegisterInvalid()){
            return R.string.error_register_invalid_request
        }

        return userService.registerUser(
            user = RegisterUserDTO(
                firstName = firstName,
                lastName = lastName,
                birthDate = birthday,
                email = email,
                phoneNumber = phoneNum,
                password = password
            )
        )[0]

    }

    fun isRegisterInvalid(): Boolean {
        return isFirstNameInvalid() ||
                isLastNameInvalid()  ||
                isBirthDateInvalid()  ||
                isEmailInvalid()    ||
                isPhoneInvalid() ||
                isPasswordInvalid() ||
                isConfirmPasswordDiff()

    }

    fun isFirstNameInvalid() : Boolean{
        return isInvalidWithRegex(NAME_REG, firstName)
    }

    fun isLastNameInvalid() : Boolean{
        return isInvalidWithRegex(NAME_REG, lastName)
    }

    fun isBirthDateInvalid() : Boolean{
        return isInvalidWithRegex(DATE_REG, birthday)
    }

    fun isEmailInvalid() : Boolean{
        return isInvalidWithRegex(EMAIL_REG, email)
    }

    fun isPhoneInvalid() : Boolean{
        return isInvalidWithRegex(PHONE_REG, phoneNum)
    }

    fun isPasswordInvalid() : Boolean{
        return isInvalidWithRegex(PASSWORD_REG, password)
    }

    fun isConfirmPasswordDiff() : Boolean{
        return confirmPassword != password
    }

    private fun isInvalidWithRegex(regex: String, str: String): Boolean{
        return !Pattern.matches(regex, str)
    }
}