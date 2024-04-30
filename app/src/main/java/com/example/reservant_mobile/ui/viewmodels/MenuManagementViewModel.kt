package com.example.reservant_mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuDTO
import com.example.reservant_mobile.data.services.IRestaurantMenuService
import com.example.reservant_mobile.data.services.RestaurantMenuService
import kotlinx.coroutines.launch

class MenuManagementViewModel(
    private val restaurantId: Int,
    private val service: IRestaurantMenuService = RestaurantMenuService()
): ViewModel() {

    var menus: MutableList<RestaurantMenuDTO> = mutableListOf()

    init {
        viewModelScope.launch {
            fetchMenus()
        }
    }

    private suspend fun fetchMenus(){
        menus = service.getMenus(restaurantId).value?.toMutableList() ?: mutableListOf()
    }

    suspend fun addMenu(menu: RestaurantMenuDTO){
        val result = service.addMenu(restaurantId, menu)

        if (!result.isError){
            menus.add(menu)
        }
    }
}