package com.example.reservant_mobile.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.services.FileService
import com.example.reservant_mobile.data.services.IRestaurantMenuService
import com.example.reservant_mobile.data.services.RestaurantMenuService
import kotlinx.coroutines.launch

class MenuManagementViewModel(
    private val restaurantId: Int,
    private val service: IRestaurantMenuService = RestaurantMenuService(),
    private val fileService: FileService = FileService()
): ViewModel() {

    var menus by mutableStateOf<List<RestaurantMenuDTO>>(emptyList())

    var isFetching by mutableStateOf(true)
    var isSaving by mutableStateOf(false)

    var name = FormField(RestaurantMenuDTO::name.name)
    var alternateName = FormField(RestaurantMenuDTO::alternateName.name)
    var menuType = FormField(RestaurantMenuDTO::menuType.name)
    var dateFrom = FormField(RestaurantMenuDTO::dateFrom.name)
    var dateUntil = FormField(RestaurantMenuDTO::dateUntil.name)

    var fetchResult: Result<List<RestaurantMenuDTO>?> by mutableStateOf(Result(isError = false, value = null))
    var result by mutableStateOf(Result(isError = false, value = null))

    init {
        viewModelScope.launch {
            isFetching = true
            fetchMenus()
            isFetching = false
        }
    }

    private suspend fun fetchMenus(){
        fetchResult = service.getMenus(restaurantId)

        if (!fetchResult.isError){
            menus = fetchResult.value!!.toMutableList()
        }
    }

    suspend fun getPhoto(menu: RestaurantMenuDTO): Bitmap? {
        val photoString = menu.photo.substringAfter("uploads/")
        val result = fileService.getImage(photoString)
        if (!result.isError){
            return result.value!!
        }
        return null
    }

    private fun createMenuDTO(menuId: Int? = null): RestaurantMenuDTO{
        return RestaurantMenuDTO(
            menuId = menuId,
            name = name.value,
            restaurantId = restaurantId,
            alternateName = alternateName.value.ifEmpty { null },
            menuType = menuType.value,
            dateFrom = dateFrom.value,
            dateUntil = dateUntil.value.ifEmpty { null },
            photo = "exampleImage"
        )
    }

    suspend fun addMenu(){
        isSaving = true

        val menu = createMenuDTO()

        val result = service.addMenu(menu)
        this.result.isError = result.isError

        if (result.isError){
            this.result.errors = result.errors
        } else {
            fetchMenus()
        }

        isSaving = false
    }

    suspend fun editMenu(menu: RestaurantMenuDTO) {
        isSaving = true

        val editedMenu = createMenuDTO(menu.menuId)

        val result = service.editMenu(editedMenu.menuId!!, editedMenu)
        println("returned: ${result.isError}")
        this.result.isError = result.isError

        if (result.isError){
            this.result.errors = result.errors
        } else {
            fetchMenus()
        }

        isSaving = false
    }

    suspend fun deleteMenu(id: Int){
        isSaving = true

        val result = service.deleteMenu(id)

        this.result.isError = result.isError

        if (result.isError){
            this.result.errors = result.errors
        } else {
            fetchMenus()
        }

        isSaving = false
    }

    fun clearFields(){
        name.value = ""
        alternateName.value = ""
        menuType.value = ""
        dateFrom.value = ""
        dateUntil.value = ""
    }

    fun <T> getToastError(result: Result<T>): Int {
        return FormField("TOAST").getError(result)
    }
}