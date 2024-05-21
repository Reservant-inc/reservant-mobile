package com.example.reservant_mobile.ui.viewmodels

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
            name = name.value,
            restaurantId = restaurantId,
            alternateName = alternateName.value,
            menuType = menuType.value,
            dateFrom = dateFrom.value,
            dateUntil = dateUntil.value
        )

        val result = service.addMenu(menu)

        if(!result.isError){
            fetchMenus()
        }

    }

    suspend fun editMenu(menu: RestaurantMenuDTO) {
        val editedMenu = RestaurantMenuDTO(
            id = menu.id,
            name = name.value,
            restaurantId = menu.restaurantId,
            alternateName = alternateName.value.ifEmpty { null },
            menuType = menuType.value,
            dateFrom = dateFrom.value,
            dateUntil = dateUntil.value
        )

        val result = service.editMenu(editedMenu.id!!, editedMenu)

        if (!result.isError){
            fetchMenus()
        }

    }

    suspend fun deleteMenu(id: Int){
        val result = service.deleteMenu(id)

        if (!result.isError){
            fetchMenus()
        }
    }

    fun clearFields(){
        name.value = ""
        alternateName.value = ""
        menuType.value = ""
        dateFrom.value = ""
        dateUntil.value = ""
    }
}