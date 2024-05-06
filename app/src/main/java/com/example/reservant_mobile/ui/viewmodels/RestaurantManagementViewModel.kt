package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantGroupDTO
import com.example.reservant_mobile.data.services.IRestaurantService
import com.example.reservant_mobile.data.services.RestaurantService
import kotlinx.coroutines.launch


class RestaurantManagementViewModel(private val restaurantService: IRestaurantService = RestaurantService()) : ViewModel() {

    var groups: List<RestaurantGroupDTO>? by mutableStateOf(listOf())
    private var selectedRestaurant: RestaurantDTO? by mutableStateOf(null)

    suspend fun initialize() {
        loadGroups()
    }

    private suspend fun loadGroups(){
        groups = restaurantService.getGroups().value;
    }

    suspend fun getGroup(groupId: Int): RestaurantGroupDTO? {
        return restaurantService.getGroup(groupId).value
    }

    fun selectRestaurant(restaurant: RestaurantDTO) {
        selectedRestaurant = restaurant
    }

    suspend fun getSingleRestaurant(id: Int): RestaurantDTO? {
        return restaurantService.getRestaurant(id).value
    }

    suspend fun deleteRestaurant(id: Int) {
        restaurantService.deleteRestaurant(id)
        this.initialize() // Reloading groups
    }

}