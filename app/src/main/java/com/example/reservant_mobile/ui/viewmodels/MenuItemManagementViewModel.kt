package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.services.IRestaurantMenuService
import com.example.reservant_mobile.data.services.RestaurantMenuService
import kotlinx.coroutines.launch

class MenuItemManagementViewModel(
    private val menuId: Int,
    private val restaurantId: Int,
    private val service: IRestaurantMenuService = RestaurantMenuService()
): ViewModel() {
    var items by mutableStateOf<List<RestaurantMenuItemDTO>>(emptyList())

    val name: FormField = FormField(RestaurantMenuItemDTO::name.name)
    val alternateName: FormField = FormField(RestaurantMenuItemDTO::alternateName.name)
    val price: FormField = FormField(RestaurantMenuItemDTO::stringPrice.name)
    val alcoholPercentage: FormField = FormField(RestaurantMenuItemDTO::stringAlcoholPercentage.name)
    val photo: FormField = FormField(RestaurantMenuItemDTO::photoFileName.name)



    init {
        viewModelScope.launch {
            fetchMenuItems()
        }
    }

    private suspend fun fetchMenuItems(){
        items = service.getMenu(menuId).value?.menuItems?.toMutableList() ?: mutableListOf()
        items.forEach{item ->
            price.value = item.price.toString()
            alcoholPercentage.value = item.alcoholPercentage.toString()
        }
    }

    private fun createMenuItemDTO(menuItemId: Int? = null): RestaurantMenuItemDTO {
        return RestaurantMenuItemDTO(
            menuItemId  = menuItemId,
            name = name.value,
            restaurantId = restaurantId,
            alternateName = alternateName.value.ifEmpty { null },
            price = price.value.toDouble(),
            alcoholPercentage = alcoholPercentage.value.toDoubleOrNull(),
            photoFileName = photo.value
        )
    }



    suspend fun createMenuItem(){
        val menuitem = createMenuItemDTO()

        val result = service.createMenuItem(menuitem)

        if(!result.isError){
            val menuItemId = result.value?.menuItemId
            val resultadd = service.addItemsToMenu(menuId, listOf(menuItemId!!))

            if(!resultadd.isError){
                fetchMenuItems()
            }
        }

    }

    suspend fun addMenuItemToMenu(){
        val menuitem = createMenuItemDTO()

        val result = service.createMenuItem(menuitem)

        if(!result.isError){
            fetchMenuItems()
        }

    }

    suspend fun deleteMenuItem(id: Int){
        val result = service.deleteMenuItem(id)

        if (!result.isError){
            fetchMenuItems()
        }
    }

    suspend fun editMenuItem(menuItem: RestaurantMenuItemDTO) {
        val editedMenuItem = createMenuItemDTO(menuItem.menuItemId)

        val result = service.editMenuItem(menuItem.menuItemId!!, editedMenuItem)

        if (!result.isError){
            fetchMenuItems()
        }

    }

    fun clearFields(){
        name.value = ""
        alternateName.value = ""
        price.value = ""
        alcoholPercentage.value = ""
        photo.value = ""
    }
}