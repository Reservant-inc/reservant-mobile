package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.Regex
import reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.services.UserService
import java.util.regex.Pattern

class EmployeeViewModel(
    private val restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService(),
    private val userService: IUserService = UserService()
) : ReservantViewModel() {
    var employees by mutableStateOf<List<RestaurantEmployeeDTO>>(emptyList())
    var isLoading by mutableStateOf(false)
    var result by mutableStateOf(Result(isError = false, value = false))


    val login: FormField = FormField(RestaurantEmployeeDTO::login.name)
    val firstName: FormField = FormField(RestaurantEmployeeDTO::firstName.name)
    val lastName: FormField = FormField(RestaurantEmployeeDTO::lastName.name)
    val password: FormField = FormField(RestaurantEmployeeDTO::password.name)
    val phoneNum: FormField = FormField(RestaurantEmployeeDTO::phoneNumber.name)

    val id: FormField = FormField(RestaurantEmployeeDTO::employeeId.name)
    var isHallEmployee by mutableStateOf(false)
    var isBackdoorEmployee by mutableStateOf(false)

    init {
        viewModelScope.launch {
            fetchEmployees()
        }
    }

    private suspend fun fetchEmployees() {
        viewModelScope.launch {
            isLoading = true
            try {
                val result: Result<List<RestaurantEmployeeDTO>?> =
                    restaurantService.getEmployees(restaurantId)
                if (!result.isError && result.value != null) {
                    employees = result.value
                }
            } catch (e: Exception) {
                result.isError = true
            } finally {
                isLoading = false
            }
        }
    }

    fun clearFields() {
        login.value = ""
        firstName.value = ""
        lastName.value = ""
        password.value = ""
        phoneNum.value = ""
        id.value = ""
        isHallEmployee = false
        isBackdoorEmployee = false
    }

    fun deleteEmployee(employee: RestaurantEmployeeDTO) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = employee.employmentId?.let { restaurantService.deleteEmployment(it) }
                if (result != null) {
                    if (!result.isError && result.value) {
                        employees = employees.filter { it.employmentId != employee.employmentId }
                    }
                }
            } catch (e: Exception) {
                result.isError = true
            } finally {
                isLoading = false
            }
        }
    }

    //TODO prawdopodobnie dodać email i date urodzenia ale najpierw trzeba skonsultować z backndem
    suspend fun editEmployee(employee: RestaurantEmployeeDTO): Boolean {
        if (isEditInvalid()) {
            return false
        }
        val newEmployee = RestaurantEmployeeDTO(
            login = login.value,
            firstName = firstName.value,
            lastName = lastName.value,
            phoneNumber = phoneNum.value,
            password = password.value
        )
        val response = restaurantService.editEmployee(employee.employeeId, newEmployee)

        val position = if (response.value != null) {
            RestaurantEmployeeDTO(
                employmentId = employee.employmentId,
                isHallEmployee = isHallEmployee,
                isBackdoorEmployee = isBackdoorEmployee
            )
        } else {
            return false
        }

        //TODO brakuje końcówki w serwisie
//        result = restaurantService.editRestaurant(restaurantId, position)
        if (result.value) {
            viewModelScope.launch {
                fetchEmployees()
            }
        }
        return result.value
    }


    suspend fun register(): Boolean {

        if (isRegisterInvalid()) {
            return false
        }

        val employee = RestaurantEmployeeDTO(
            login = login.value,
            firstName = firstName.value,
            lastName = lastName.value,
            phoneNumber = phoneNum.value,
            password = password.value
        )

        val response = restaurantService.createEmployee(employee)

        val position = if (response.value != null) {
            RestaurantEmployeeDTO(
                //TODO prawodopodobnie do zmiany na employeeId jak wejdzie zmiana na Backendzie
                employeeId = response.value.userId,
                isHallEmployee = isHallEmployee,
                isBackdoorEmployee = isBackdoorEmployee
            )
        } else {
            return false
        }

        result = restaurantService.addEmployeeToRestaurant(restaurantId, position)
        if (result.value) {
            viewModelScope.launch {
                fetchEmployees()
            }
        }
        return result.value
    }

    private fun isRegisterInvalid(): Boolean {
        return isLoginInvalid() ||
                isFirstNameInvalid() ||
                isLastNameInvalid() ||
                isPhoneInvalid() ||
                isPasswordInvalid()
    }

    private fun isEditInvalid(): Boolean {
        return isLoginInvalid() ||
                isFirstNameInvalid() ||
                isLastNameInvalid() ||
                isPhoneInvalid()
    }

    fun isLoginInvalid(): Boolean {
        return isInvalidWithRegex(Regex.LOGIN, login.value) ||
                getFieldError(result, login.name) != -1
    }


    fun isFirstNameInvalid(): Boolean {
        return isInvalidWithRegex(Regex.NAME_REG, firstName.value) ||
                getFieldError(result, firstName.name) != -1
    }

    fun isLastNameInvalid(): Boolean {
        return isInvalidWithRegex(Regex.NAME_REG, lastName.value) ||
                getFieldError(result, lastName.name) != -1
    }

    fun isPhoneInvalid(): Boolean {
        return isInvalidWithRegex(Regex.PHONE_REG, phoneNum.value) ||
                getFieldError(result, phoneNum.name) != -1
    }

    fun isPasswordInvalid(): Boolean {
        return isInvalidWithRegex(Regex.PASSWORD_REG, password.value) ||
                getFieldError(result, password.name) != -1
    }

    private fun isInvalidWithRegex(regex: String, str: String): Boolean {
        return !Pattern.matches(regex, str)
    }

    fun getLoginError(): Int {
        return getFieldError(result, login.name)
    }

    fun getFirstNameError(): Int {
        return getFieldError(result, firstName.name)
    }

    fun getLastNameError(): Int {
        return getFieldError(result, lastName.name)
    }

    fun getPhoneError(): Int {
        return getFieldError(result, phoneNum.name)
    }

    fun getPasswordError(): Int {
        return getFieldError(result, password.name)
    }

    fun getToastError(): Int {
        return getToastError(result)
    }
}
