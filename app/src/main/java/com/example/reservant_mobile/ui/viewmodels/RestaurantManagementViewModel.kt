package com.example.reservant_mobile.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantGroupDTO
import com.example.reservant_mobile.data.services.FileService
import com.example.reservant_mobile.data.services.IRestaurantService
import com.example.reservant_mobile.data.services.RestaurantService
import kotlinx.coroutines.launch


class RestaurantManagementViewModel(private val restaurantService: IRestaurantService = RestaurantService()) : ViewModel() {

    var groups: List<RestaurantGroupDTO>? by mutableStateOf(listOf())
    var selectedRestaurant: RestaurantDTO? by mutableStateOf(null)
    var selectedRestaurantLogo: Bitmap? by mutableStateOf(null)

    private val fileService = FileService()


    init {
        viewModelScope.launch {
            loadGroups()
        }
    }

    private suspend fun loadGroups(){
        groups = restaurantService.getGroups().value;
    }

    suspend fun getGroup(groupId: Int): RestaurantGroupDTO? {
        return restaurantService.getGroup(groupId).value
    }

    suspend fun getPhoto(restaurant: RestaurantDTO): Bitmap? {
        if(restaurant.logo == null)
            return null

        val photoString = restaurant.logo.substringAfter("uploads/")
        val result = fileService.getImage(photoString)
        if (!result.isError){
            return result.value!!
        }
        return null
    }


    suspend fun getSingleRestaurant(id: Int): RestaurantDTO? {
        return restaurantService.getRestaurant(id).value
    }

    suspend fun deleteRestaurant(id: Int) {
        restaurantService.deleteRestaurant(id)
        loadGroups() // Reloading groups
    }

}