package reservant_mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import reservant_mobile.data.models.dtos.LoginCredentialsDTO
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.LocalBearerService
import reservant_mobile.data.services.UserService

class LoginViewModel(private val userService: IUserService = UserService()) : ViewModel() {

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
                getFieldError(login.name) != -1
    }

    private fun isPasswordInvalid(): Boolean{
        return isInvalid(password.value) ||
                getFieldError(password.name) != -1
    }

    private fun isFormInvalid(): Boolean {
        return isLoginInvalid() || isPasswordInvalid()
    }

    private fun isInvalid(str: String) : Boolean{
        return str.isBlank()
    }

    private fun getFieldError(name: String): Int{
        if(!result.isError){
            return -1
        }

        return result.errors!!.getOrDefault(name, -1)
    }

    fun getToastError(): Int{
        return getFieldError("TOAST")
    }
}