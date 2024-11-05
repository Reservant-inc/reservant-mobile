package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import reservant_mobile.data.utils.getCountriesList

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
    val birthday: FormField = FormField(RestaurantEmployeeDTO::birthDate.name)
    val password: FormField = FormField(RestaurantEmployeeDTO::password.name)
    val phoneNum: FormField = FormField(RestaurantEmployeeDTO::phoneNumber.name)

    var mobileCountry by mutableStateOf(getCountriesList().firstOrNull { it.nameCode == "pl" })

    private var phoneNumberWithCountryCode: String = "+${mobileCountry!!.code}${phoneNum.value}"

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
                    restaurantService.getMyEmployees(restaurantId)
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
        birthday.value = ""
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

    suspend fun editEmployee(employee: RestaurantEmployeeDTO): Boolean {
        if (isEditInvalid()) {
            return false
        }
        val newEmployee = RestaurantEmployeeDTO(
            firstName = firstName.value,
            lastName = lastName.value,
            birthDate = birthday.value,
            phoneNumber = phoneNum.value
        )
        val response = employee.employeeId?.let { restaurantService.editEmployee(it, newEmployee) }

        val position = if (response?.value != null) {
            listOf(RestaurantEmployeeDTO(
                employmentId = employee.employmentId,
                isHallEmployee = isHallEmployee,
                isBackdoorEmployee = isBackdoorEmployee
            ))
        } else {
            return false
        }

        result = restaurantService.editEmployment(position)
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
            birthDate = birthday.value,
            phoneNumber = phoneNum.value,
            password = password.value
        )

        val response = restaurantService.createEmployee(employee)

        val position = if (response.value != null) {
            RestaurantEmployeeDTO(
                employeeId = response.value.userId,
                isHallEmployee = isHallEmployee,
                isBackdoorEmployee = isBackdoorEmployee
            )
        } else {
            return false
        }

        result = restaurantService.addEmployeeToRestaurant(restaurantId, listOf(position))
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
                isPasswordInvalid() ||
                isBirthDateInvalid()
    }

    private fun isEditInvalid(): Boolean {
        return  isFirstNameInvalid() ||
                isLastNameInvalid() ||
                isPhoneInvalid() ||
                isBirthDateInvalid()
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

    fun isBirthDateInvalid() : Boolean{
        return isInvalidWithRegex(Regex.DATE_REG, birthday.value) ||
                getFieldError(result, birthday.name) != -1
    }

    fun isPhoneInvalid(): Boolean {
        return isInvalidWithRegex(Regex.PHONE_REG, phoneNum.value) ||
                getFieldError(result, phoneNum.name) != -1
    }

    fun isPasswordInvalid(): Boolean {
        return isInvalidWithRegex(Regex.PASSWORD_REG, password.value) ||
                getFieldError(result, password.name) != -1
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

    fun getBirthDateError(): Int{
        return getFieldError(result, birthday.name)
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
