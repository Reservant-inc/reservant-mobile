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

    var result: Result<RestaurantDTO?> by mutableStateOf(Result(isError = false, value=null))

    var restaurant: RestaurantDTO? by mutableStateOf(null)
    var isLoading: Boolean by mutableStateOf(false)

    init {
        viewModelScope.launch {
            loadRestaurant(restaurantId)
        }
    }

    private fun loadRestaurant(id: Int) {
        viewModelScope.launch {
            isLoading = true

            try {
                result = restaurantService.getRestaurant(id)

                if (!result.isError) {
                    restaurant = result.value
                }

            } catch (e: Exception) {
                println("[LOAD RESTAURANTS ERROR]" + e.message)
            } finally {
                isLoading = false
            }
        }
    }

    public fun getToastError(): Int{
        if(!result.isError){
            return -1
        }

        return result.errors!!.getOrDefault("TOAST", -1)
    }

}