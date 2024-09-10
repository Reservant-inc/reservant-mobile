package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import reservant_mobile.data.constants.Regex
import reservant_mobile.data.models.dtos.RegisterUserDTO
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.getCountriesList
import java.util.regex.Pattern


class RegisterViewModel(
    private val userService: IUserService = UserService()
) : ReservantViewModel() {

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

    var isLoginUnique by mutableStateOf(true)
    
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

    suspend fun checkLoginUnique(){
        isLoginUnique = userService.isLoginUnique(login.value)
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
                getFieldError(result, login.name) != -1
    }


    fun isFirstNameInvalid() : Boolean{
        return isInvalidWithRegex(Regex.NAME_REG, firstName.value) ||
                getFieldError(result, firstName.name) != -1
    }

    fun isLastNameInvalid() : Boolean{
        return isInvalidWithRegex(Regex.NAME_REG, lastName.value) ||
                getFieldError(result, lastName.name) != -1
    }

    fun isBirthDateInvalid() : Boolean{
        return isInvalidWithRegex(Regex.DATE_REG, birthday.value) ||
                getFieldError(result, birthday.name) != -1
    }

    fun isEmailInvalid() : Boolean{
        return isInvalidWithRegex(Regex.EMAIL_REG, email.value) ||
                getFieldError(result, email.name) != -1
    }

    fun isPhoneInvalid() : Boolean{
        return isInvalidWithRegex(Regex.PHONE_REG, phoneNum.value) ||
                getFieldError(result, phoneNum.name) != -1
    }

    fun isPasswordInvalid() : Boolean{
        return isInvalidWithRegex(Regex.PASSWORD_REG, password.value) ||
                getFieldError(result, password.name) != -1
    }

    fun isConfirmPasswordDiff() : Boolean{
        return confirmPassword.value != password.value
    }

    fun getLoginError(): Int{
        return getFieldError(result, login.name)
    }

    fun getFirstNameError(): Int{
        return getFieldError(result, firstName.name)
    }

    fun getLastNameError(): Int{
        return getFieldError(result, lastName.name)
    }

    fun getBirthDateError(): Int{
        return getFieldError(result, birthday.name)
    }

    fun getEmailError(): Int{
        return getFieldError(result, email.name)
    }

    fun getPhoneError(): Int{
        return getFieldError(result, phoneNum.name)
    }

    fun getPasswordError(): Int{
        return getFieldError(result, password.name)
    }

    fun getToastError(): Int{
        return getToastError(result)
    }

}