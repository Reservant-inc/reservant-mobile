package reservant_mobile.ui.viewmodels

import reservant_mobile.data.models.dtos.LoginCredentialsDTO
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.LocalBearerService
import reservant_mobile.data.services.UserService

class LoginViewModel(
    private val userService: IUserService = UserService()
) : ReservantViewModel() {

    var result: Result<Boolean> = Result(isError = false, value = false)
    var login: FormField = FormField(LoginCredentialsDTO::login.name)
    var password: FormField = FormField(LoginCredentialsDTO::password.name)

    suspend fun login(): Boolean{

        if (isFormInvalid()){
            return false
        }

        result = userService.loginUser(
            LoginCredentialsDTO(
                login = login.value,
                password = password.value,
                rememberMe = true
            )
        )

        return result.value
    }

    suspend fun logout(){
        userService.logoutUser()
    }

    suspend fun refreshToken(): Boolean{
        return LocalBearerService().getBearerToken().isNotEmpty() && userService.refreshToken()
    }
    
    private fun isLoginInvalid(): Boolean{
        return isInvalid(login.value) ||
                getFieldError(result, login.name) != -1
    }

    private fun isPasswordInvalid(): Boolean{
        return isInvalid(password.value) ||
                getFieldError(result, password.name) != -1
    }

    private fun isFormInvalid(): Boolean {
        return isLoginInvalid() || isPasswordInvalid()
    }

    private fun isInvalid(str: String) : Boolean{
        return str.isBlank()
    }

    fun getToastError(): Int{
        return getToastError(result)
    }
}