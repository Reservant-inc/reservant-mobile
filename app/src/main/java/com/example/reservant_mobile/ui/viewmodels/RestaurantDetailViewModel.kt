package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.services.IRestaurantMenuService
import com.example.reservant_mobile.data.services.IRestaurantService
import com.example.reservant_mobile.data.services.RestaurantMenuService
import com.example.reservant_mobile.data.services.RestaurantService
import kotlinx.coroutines.launch

class RestaurantDetailViewModel(
    private var restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService(),
    private val menuService: IRestaurantMenuService = RestaurantMenuService()
) : ViewModel() {

    var resultRestaurant: Result<RestaurantDTO?> by mutableStateOf(Result(isError = false, value=null))
    var resultMenus: Result<List<RestaurantMenuDTO>?> by mutableStateOf(Result(isError = false, value=null))

    var restaurant: RestaurantDTO? by mutableStateOf(null)
    var menus: List<RestaurantMenuDTO>? by mutableStateOf(emptyList())
    var currentMenu: RestaurantMenuDTO? by mutableStateOf(null)
    var isLoading: Boolean by mutableStateOf(false)

    init {
        viewModelScope.launch {
            loadRestaurantAndMenus(restaurantId)
        }
    }

    private suspend fun loadRestaurantAndMenus(id: Int) {
        isLoading = true
        val restaurantLoaded = loadRestaurant(id)
        if (restaurantLoaded) {
            loadMenus(id)
            menus?.firstOrNull()?.menuId?.let { loadFullMenu(it) }
        }
        isLoading = false
    }

    suspend fun loadRestaurant(id: Int): Boolean {
        if (id != restaurantId) {
            restaurantId = id
        }
        resultRestaurant = restaurantService.getRestaurant(restaurantId)
        if (resultRestaurant.isError) {
            return false
        }
        restaurant = resultRestaurant.value
        return true
    }

    private suspend fun loadMenus(id: Int) {
        resultMenus = menuService.getMenus(id)
        if (!resultMenus.isError) {
            menus = resultMenus.value
        }
    }

    suspend fun loadFullMenu(menuId: Int) {
        val result = menuService.getMenu(menuId)
        if (!result.isError) {
            currentMenu = result.value
        }
    }

    fun getToastError(): Int {
        if (!resultRestaurant.isError) {
            return -1
        }
        return resultRestaurant.errors?.getOrDefault("TOAST", -1) ?: -1
    }
}