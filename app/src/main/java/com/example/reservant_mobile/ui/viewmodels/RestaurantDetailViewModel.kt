package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.services.IRestaurantService
import com.example.reservant_mobile.data.services.RestaurantService
import kotlinx.coroutines.launch

class RestaurantDetailViewModel(
    private val restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService()
) : ViewModel() {

    var restaurant: RestaurantDTO? by mutableStateOf(null)
    var isLoading: Boolean by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)
    var errorToast: Int? by mutableStateOf(null)

    init {
        viewModelScope.launch {
            loadRestaurant(restaurantId)
        }
    }

    private fun loadRestaurant(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            errorToast = null
            try {
                val response = restaurantService.getRestaurant(id)

                if (!response.isError) {
                    restaurant = response.value
                } else {
                    response.errors?.get("TOAST")?.let {
                        errorToast = it
                    } ?: run {
                        errorMessage = "Error while loading restaurant: ${response.errors}"
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

}