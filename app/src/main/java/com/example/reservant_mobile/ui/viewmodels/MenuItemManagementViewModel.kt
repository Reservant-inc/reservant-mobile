package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import com.example.reservant_mobile.data.services.IRestaurantMenuService
import com.example.reservant_mobile.data.services.RestaurantMenuService
import kotlinx.coroutines.launch

class MenuItemManagementViewModel(
    private val menuId: Int,
    private val service: IRestaurantMenuService = RestaurantMenuService()
): ViewModel() {

    var items by mutableStateOf<List<RestaurantMenuItemDTO>>(emptyList())

    init {
        viewModelScope.launch {
            fetchMenuItems()
        }
    }

    private suspend fun fetchMenuItems(){
        items = service.getMenu(menuId).value?.menuItems?.toMutableList() ?: mutableListOf()
    }



    suspend fun addMenu(menu: RestaurantMenuItemDTO){
        TODO()
    }

    fun deleteMenu(menu: RestaurantMenuItemDTO){
        items = items.filter {
            it != menu
        }
    }
}