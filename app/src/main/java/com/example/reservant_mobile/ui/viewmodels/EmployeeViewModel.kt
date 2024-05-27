package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.services.IRestaurantService
import com.example.reservant_mobile.data.services.RestaurantService
import com.example.reservant_mobile.data.constants.Regex
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class EmployeeViewModel(
    private val restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService()
) : ViewModel() {
    var employees by mutableStateOf<List<RestaurantEmployeeDTO>>(emptyList())
    var isLoading by mutableStateOf(false)
    var result by mutableStateOf(Result(isError = false, value = false))


    val login: FormField = FormField(RestaurantEmployeeDTO::login.name)
    val firstName: FormField = FormField(RestaurantEmployeeDTO::firstName.name)
    val lastName: FormField = FormField(RestaurantEmployeeDTO::lastName.name)
    val password: FormField = FormField(RestaurantEmployeeDTO::password.name)
    val phoneNum: FormField = FormField(RestaurantEmployeeDTO::phoneNumber.name)

    val id: FormField = FormField(RestaurantEmployeeDTO::userId.name)
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

    fun deleteEmployee(employee: RestaurantEmployeeDTO) {
        employees = employees.filter {
            it != employee
        }
    }

    fun editEmployee(employee: RestaurantEmployeeDTO) {
        employees = employees.map { existingEmployee ->
            if (existingEmployee.userId == employee.userId) {
                RestaurantEmployeeDTO(
                    login = login.value,
                    firstName = firstName.value,
                    lastName = lastName.value,
                    phoneNumber = phoneNum.value,
                    password = password.value,
                    isHallEmployee = isHallEmployee,
                    isBackdoorEmployee = isBackdoorEmployee
                )
            } else {
                existingEmployee
            }
        }
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
                userId = response.value.userId,
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

    fun isRegisterInvalid(): Boolean {
        return isLoginInvalid() ||
                isFirstNameInvalid() ||
                isLastNameInvalid() ||
                isPasswordInvalid()
    }

    fun isLoginInvalid(): Boolean {
        return isInvalidWithRegex(Regex.LOGIN, login.value) ||
                getFieldError(login.name) != -1
    }


    fun isFirstNameInvalid(): Boolean {
        return isInvalidWithRegex(Regex.NAME_REG, firstName.value) ||
                getFieldError(firstName.name) != -1
    }

    fun isLastNameInvalid(): Boolean {
        return isInvalidWithRegex(Regex.NAME_REG, lastName.value) ||
                getFieldError(lastName.name) != -1
    }

    fun isPhoneInvalid(): Boolean {
        return isInvalidWithRegex(Regex.PHONE_REG, phoneNum.value) ||
                getFieldError(phoneNum.name) != -1
    }

    fun isPasswordInvalid(): Boolean {
        return isInvalidWithRegex(Regex.PASSWORD_REG, password.value) ||
                getFieldError(password.name) != -1
    }

    private fun isInvalidWithRegex(regex: String, str: String): Boolean {
        return !Pattern.matches(regex, str)
    }

    private fun getFieldError(name: String): Int {
        if (!result.isError) {
            return -1
        }

        return result.errors!!.getOrDefault(name, -1)
    }

    fun getLoginError(): Int {
        return getFieldError(login.name)
    }

    fun getFirstNameError(): Int {
        return getFieldError(firstName.name)
    }

    fun getLastNameError(): Int {
        return getFieldError(lastName.name)
    }

    fun getPhoneError(): Int {
        return getFieldError(phoneNum.name)
    }

    fun getPasswordError(): Int {
        return getFieldError(password.name)
    }

    fun getToastError(): Int {
        return getFieldError("TOAST")
    }
}
