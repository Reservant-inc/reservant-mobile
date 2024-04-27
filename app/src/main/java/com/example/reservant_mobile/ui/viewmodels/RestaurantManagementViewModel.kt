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


class RestaurantManagementViewModel(private val restaurantService: IRestaurantService = RestaurantService()) : ViewModel() {


    var restaurants: List<RestaurantDTO>? by mutableStateOf(listOf())
    private var selectedRestaurant: RestaurantDTO? by mutableStateOf(null)


    suspend fun loadRestaurants() {
        restaurants = restaurantService.getRestaurants().value
    }

    fun selectRestaurant(restaurant: RestaurantDTO) {
        selectedRestaurant = restaurant
    }

    suspend fun getSingleRestaurant(id: Int): RestaurantDTO? {
        return restaurantService.getRestaurant(id).value
    }


    fun deleteSelectedRestaurant() {
        selectedRestaurant?.let { restaurant ->
            viewModelScope.launch {
                restaurantService.deleteRestaurant(restaurant.id)
                loadRestaurants()
            }
        }
    }

}