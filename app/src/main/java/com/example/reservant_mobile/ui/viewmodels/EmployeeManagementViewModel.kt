// Employee ViewModel
package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.reservant_mobile.data.services.IRestaurantService
import com.example.reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.services.RestaurantService

class EmployeeViewModel(private val restaurantService: IRestaurantService = RestaurantService()) : ViewModel() {
    var employees by mutableStateOf<List<RestaurantEmployeeDTO>>(emptyList())
    var isLoading by mutableStateOf(true)
    var hasError by mutableStateOf(false)
    val restaurantId by mutableStateOf(1)

    init {
        fetchEmployees()
    }

    private fun fetchEmployees() {
        viewModelScope.launch {
            isLoading = true
            try {
                val result: Result<List<RestaurantEmployeeDTO>?> = restaurantService.getEmployees(restaurantId)
                if (!result.isError && result.value != null) {
                    employees = result.value
                    hasError = false
                } else {
                    hasError = true
                }
            } catch (e: Exception) {
                hasError = true
            } finally {
                isLoading = false
            }
        }
    }
}
