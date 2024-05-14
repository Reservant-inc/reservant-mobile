package com.example.reservant_mobile.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.data.services.IRestaurantMenuService
import com.example.reservant_mobile.data.services.RestaurantMenuService
import kotlinx.coroutines.launch

class MenuManagementViewModel(
    private val restaurantId: Int,
    private val service: IRestaurantMenuService = RestaurantMenuService()
): ViewModel() {

    var menus by mutableStateOf<List<RestaurantMenuDTO>>(emptyList())

    var name = FormField(RestaurantMenuDTO::name.name)
    var alternateName = FormField(RestaurantMenuDTO::alternateName.name)
    var menuType = FormField(RestaurantMenuDTO::menuType.name)
    var dateFrom = FormField(RestaurantMenuDTO::dateFrom.name)
    var dateUntil = FormField(RestaurantMenuDTO::dateUntil.name)

    init {
        viewModelScope.launch {
            fetchMenus()
        }
    }

    private suspend fun fetchMenus(){
        menus = service.getMenus(restaurantId).value?.toMutableList() ?: mutableListOf()
    }

    suspend fun addMenu(){
        val menu = RestaurantMenuDTO(
            restaurantId,
            name = name.value,
            alternateName = alternateName.value,
            menuType = menuType.value,
            dateFrom = dateFrom.value,
            dateUntil = dateUntil.value
        )

        menus = menus + menu

    }

    suspend fun editMenu(menu: RestaurantMenuDTO) {
        val editedMenu = RestaurantMenuDTO(
            menu.id,
            menu.restaurantId,
            name.value,
            alternateName.value.ifEmpty { null },
            menuType.value,
            dateFrom.value,
            dateUntil.value
        )

        menus = menus.filter { it != menu } + editedMenu
    }

    fun deleteMenu(menu: RestaurantMenuDTO){
        menus = menus.filter { it != menu }
    }

    fun clearFields(){
        name.value = ""
        alternateName.value = ""
        menuType.value = ""
        dateFrom.value = ""
        dateUntil.value = ""
    }
}