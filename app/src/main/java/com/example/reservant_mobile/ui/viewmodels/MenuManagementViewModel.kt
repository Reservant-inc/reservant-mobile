package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.services.IRestaurantMenuService
import com.example.reservant_mobile.data.services.RestaurantMenuService
import kotlinx.coroutines.launch

class MenuManagementViewModel(
    private val restaurantId: Int,
    private val service: IRestaurantMenuService = RestaurantMenuService()
): ViewModel() {

    var menus by mutableStateOf<List<RestaurantMenuDTO>>(emptyList())

    var isLoading by mutableStateOf(true)

    var name = FormField(RestaurantMenuDTO::name.name)
    var alternateName = FormField(RestaurantMenuDTO::alternateName.name)
    var menuType = FormField(RestaurantMenuDTO::menuType.name)
    var dateFrom = FormField(RestaurantMenuDTO::dateFrom.name)
    var dateUntil = FormField(RestaurantMenuDTO::dateUntil.name)

    var fetchResult: Result<List<RestaurantMenuDTO>?> by mutableStateOf(Result(isError = false, value = null))
    var result: Result<RestaurantDTO?> by mutableStateOf(Result(isError = false, value = null))

    init {
        viewModelScope.launch {
            fetchMenus()
        }
    }

    private suspend fun fetchMenus(){
        isLoading = true

        fetchResult = service.getMenus(restaurantId)

        if (!fetchResult.isError){
            menus = fetchResult.value!!.toMutableList()
        }

        isLoading = false
    }

    private fun createMenuDTO(menuId: Int? = null): RestaurantMenuDTO{
        return RestaurantMenuDTO(
            menuId = menuId,
            name = name.value,
            restaurantId = restaurantId,
            alternateName = alternateName.value.ifEmpty { null },
            menuType = menuType.value,
            dateFrom = dateFrom.value,
            dateUntil = dateUntil.value.ifEmpty { null }
        )
    }

    suspend fun addMenu(){
        val menu = createMenuDTO()

        val result = service.addMenu(menu)

        if(!result.isError){
            fetchMenus()
        }

    }

    suspend fun editMenu(menu: RestaurantMenuDTO) {
        val editedMenu = createMenuDTO(menu.menuId)

        val result = service.editMenu(editedMenu.menuId!!, editedMenu)

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

    fun <T> getToastError(result: Result<T>): Int {
        return getFieldError(result, "TOAST")
    }

    private fun <T> getFieldError(result: Result<T>, name: String): Int {
        if (!result.isError) {
            return -1
        }

        return result.errors?.getOrDefault(name, -1) ?: -1
    }
}