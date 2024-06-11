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
    private var restaurantId: Int,
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

    suspend fun loadRestaurant(id: Int): Boolean{
        if (id != restaurantId || restaurant == null){
            restaurantId = id

            isLoading = true
            result = restaurantService.getRestaurant(restaurantId)
            isLoading = false

            if (result.isError) {
                return false
            }
            restaurant = result.value
        }
        return true
    }

    public fun getToastError(): Int{
        if(!result.isError){
            return -1
        }

        return result.errors!!.getOrDefault("TOAST", -1)
    }

}