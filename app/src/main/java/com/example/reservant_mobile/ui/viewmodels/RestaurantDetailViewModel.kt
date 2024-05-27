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

class RestaurantDetailViewModel(private val restaurantService: IRestaurantService = RestaurantService()) : ViewModel() {

    var restaurant: RestaurantDTO? by mutableStateOf(null)
        private set

    var isLoading: Boolean by mutableStateOf(false)
        private set

    var errorMessage: String? by mutableStateOf(null)
        private set

    fun loadRestaurant(id: Int) {
        viewModelScope.launch {
            try {
                isLoading = true
                restaurant = restaurantService.getRestaurant(id).value
            } catch (e: Exception) {
                errorMessage = "Failed to load restaurant details: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }







}