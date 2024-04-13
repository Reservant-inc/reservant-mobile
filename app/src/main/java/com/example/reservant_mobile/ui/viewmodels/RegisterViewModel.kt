package com.example.reservant_mobile.ui.viewmodels

import com.example.reservant_mobile.data.services.IUserService
import com.example.reservant_mobile.data.services.UserService
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.data.models.dtos.RegisterUserDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.utils.getCountriesList
import com.example.reservant_mobile.ui.constants.Regex
import java.util.regex.Pattern


class RegisterViewModel(private val userService: IUserService = UserService()) : ViewModel() {

    var result by mutableStateOf(Result(isError=false, value=false))

    //BACKEND VALIDATED FIELDS
    val login: FormField = FormField(RegisterUserDTO::login.name)
    val firstName: FormField = FormField(RegisterUserDTO::firstName.name)
    val lastName: FormField = FormField(RegisterUserDTO::lastName.name)
    val email: FormField = FormField(RegisterUserDTO::email.name)
    val password: FormField = FormField(RegisterUserDTO::password.name)
    val birthday: FormField = FormField(RegisterUserDTO::birthDate.name)
    val phoneNum: FormField = FormField(RegisterUserDTO::phoneNumber.name)

    //ONLY FRONTEND VALIDATED
    val confirmPassword: FormField = FormField("confirmPassword")


    val countriesList = getCountriesList()
    var mobileCountry by mutableStateOf(getCountriesList().firstOrNull { it.nameCode == "pl" })

    suspend fun register() : Boolean{

        if (isRegisterInvalid()){
            return false
        }

        val user = RegisterUserDTO(
            login = login.value,
            firstName = firstName.value,
            lastName = lastName.value,
            birthDate = birthday.value,
            email = email.value,
            password = password.value
        )

        if (!isPhoneInvalid()) user.phoneNumber = phoneNum.value

        result = userService.registerUser(user)
        return result.value
    }

    fun isRegisterInvalid(): Boolean {
        return isLoginInvalid() ||
                isFirstNameInvalid() ||
                isLastNameInvalid()  ||
                isBirthDateInvalid()  ||
                isEmailInvalid()    ||
                isPasswordInvalid() ||
                isConfirmPasswordDiff()
    }

    fun isLoginInvalid(): Boolean{
        return isInvalidWithRegex(Regex.LOGIN, login.value) ||
                (result.isError && result.errors!!.containsKey(login.name))
    }

    fun isLoginInvalid(): Boolean{

    }

    fun getLoginErrorsFromResponse(): Int {
        if (result.isError){
            var error: Int? = result.errors!!.get(login.name) ?: return -1
        }
    }

    fun isFirstNameInvalid() : Boolean{
        return isInvalidWithRegex(Regex.NAME_REG, firstName.value)
    }

    fun isLastNameInvalid() : Boolean{
        return isInvalidWithRegex(Regex.NAME_REG, lastName.value)
    }

    fun isBirthDateInvalid() : Boolean{
        return isInvalidWithRegex(Regex.DATE_REG, birthday.value)
    }

    fun isEmailInvalid() : Boolean{
        return isInvalidWithRegex(Regex.EMAIL_REG, email.value)
    }

    fun isPhoneInvalid() : Boolean{
        return isInvalidWithRegex(Regex.PHONE_REG, phoneNum.value)
    }

    fun isPasswordInvalid() : Boolean{
        return isInvalidWithRegex(Regex.PASSWORD_REG, password.value)
    }

    fun isConfirmPasswordDiff() : Boolean{
        return confirmPassword.value != password.value
    }

    private fun isInvalidWithRegex(regex: String, str: String): Boolean{
        return !Pattern.matches(regex, str)
    }
}