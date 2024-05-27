package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.services.IRestaurantService
import com.example.reservant_mobile.data.services.RestaurantService
import kotlinx.coroutines.launch

class RestaurantDetailViewModel(
    private val restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService()
) : ViewModel() {

    init {
        viewModelScope.launch {
            loadRestaurant()
        }
    }

    var restaurant: RestaurantDTO? by mutableStateOf(null)
        private set

    var isLoading: Boolean by mutableStateOf(false)
        private set

    var errorMessage: String? by mutableStateOf(null)
        private set

    private suspend fun loadRestaurant() {
        try {
            isLoading = true
            val result = restaurantService.getRestaurant(restaurantId)
            if(!result.isError){
                restaurant = result.value
            }else{
                errorMessage = "Error while fetching restaurant: ${result.errors}"
            }
        } catch (e: Exception) {
            errorMessage = "Failed to load restaurant details: ${e.message}"
        } finally {
            isLoading = false
        }
    }
}