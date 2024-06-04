package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.EventDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.services.IRestaurantMenuService
import com.example.reservant_mobile.data.services.IRestaurantService
import com.example.reservant_mobile.data.services.RestaurantMenuService
import com.example.reservant_mobile.data.services.RestaurantService
import kotlinx.coroutines.launch

class RestaurantDetailViewModel(
    private val restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService(),
    private val menuService: IRestaurantMenuService = RestaurantMenuService()
) : ViewModel() {

    var resultRestaurant: Result<RestaurantDTO?> by mutableStateOf(Result(isError = false, value=null))
    var resultMenus: Result<List<RestaurantMenuDTO>?> by mutableStateOf(Result(isError = false, value=null))

    var restaurant: RestaurantDTO? by mutableStateOf(null)
    var menus: List<RestaurantMenuDTO>? by mutableStateOf(null)
    var isLoading: Boolean by mutableStateOf(false)
    var eventsLoading: Boolean by mutableStateOf(true)


    init {
        isLoading = true
        viewModelScope.launch {
            try{
                loadRestaurant(restaurantId)
                loadMenus(restaurantId)
            } catch (e: Exception) {
                println("[LOADING RESTAURANT ERROR]" + e.message)
            } finally {
                isLoading = false
            }
            restaurant = resultRestaurant.value
        }
    }


    private fun loadRestaurant(id: Int) {
        viewModelScope.launch {

            resultRestaurant = restaurantService.getRestaurant(id)
            if (!resultRestaurant.isError) {
                restaurant = resultRestaurant.value
            }

        }
    }

    private fun loadMenus(id: Int) {
        viewModelScope.launch {

            resultMenus = menuService.getMenus(id)
            if (!resultMenus.isError) {
                menus = resultMenus.value
            }

        }
    }

    fun getToastError(): Int{
        if(!resultRestaurant.isError) {
            return -1
        }
        return resultRestaurant.errors!!.getOrDefault("TOAST", -1)
    }

}