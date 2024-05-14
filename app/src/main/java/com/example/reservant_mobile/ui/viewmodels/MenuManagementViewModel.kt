package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var menus by mutableStateOf<List<RestaurantMenuDTO>>(emptyList())

    init {
        viewModelScope.launch {
            fetchMenus()
        }
    }

    private suspend fun fetchMenus(){
        menus = service.getMenus(restaurantId).value?.toMutableList() ?: mutableListOf()
    }

    suspend fun addMenu(menu: RestaurantMenuDTO){
        val result = service.addMenu(menu)

        TODO()
    }

    fun editMenu(
        menu: RestaurantMenuDTO,
        name: String,
        altName: String,
        menuType: String,
        dateFrom: String,
        dateUntil: String
    ) {
        val editedMenu = RestaurantMenuDTO(
            menu.id,
            menu.restaurantId,
            name,
            altName.ifEmpty { null },
            menuType,
            dateFrom,
            dateUntil.ifEmpty { null }
        )

        menus = menus.filter { it != menu } + editedMenu
    }

    fun deleteMenu(menu: RestaurantMenuDTO){
        menus = menus.filter { it != menu }
    }
}