package com.example.reservant_mobile.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.constants.Regex
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.services.FileService
import com.example.reservant_mobile.data.services.IRestaurantMenuService
import com.example.reservant_mobile.data.services.RestaurantMenuService
import com.example.reservant_mobile.data.utils.getFileName
import com.example.reservant_mobile.data.utils.isFileSizeInvalid
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class MenuManagementViewModel(
    private val restaurantId: Int,
    private val service: IRestaurantMenuService = RestaurantMenuService(),
    private val fileService: FileService = FileService()
): ViewModel() {

    var menus by mutableStateOf<List<RestaurantMenuDTO>>(emptyList())

    val menuTypes = listOf("Food", "Alcohol")

    var isFetching by mutableStateOf(true)
    var isSaving by mutableStateOf(false)

    var name = FormField(RestaurantMenuDTO::name.name)
    var alternateName = FormField(RestaurantMenuDTO::alternateName.name)
    var menuType = FormField(RestaurantMenuDTO::menuType.name)
    var dateFrom = FormField(RestaurantMenuDTO::dateFrom.name)
    var dateUntil = FormField(RestaurantMenuDTO::dateUntil.name)
    var photo = FormField(RestaurantMenuDTO::photo.name)

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
            photo = photo.value
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
        photo.value = ""
    }

    fun isNameInvalid(): Boolean{
        return isInvalidWithRegex(Regex.NAME_REG, name.value)
    }

    fun isAltNameInvalid(): Boolean{
        return isInvalidWithRegex(Regex.NAME_REG, alternateName.value)
    }

    fun isMenuTypeInvalid(): Boolean {
        return menuType.value.isBlank()
    }

    fun photoErrors(context: Context): Int {
        if (photo.value.isBlank()) return R.string.error_file_not_given

        if (!getFileName(context, photo.value).endsWith(".jpg", ignoreCase = true)) return R.string.error_wrong_file_format

        return -1
    }

    fun isPhotoTooLarge(context: Context): Int {
        if (photo.value.isBlank()) return -1
        if (isFileSizeInvalid(context, photo.value)) return R.string.error_registerRestaurant_invalid_file
        return -1
    }

    private fun isInvalidWithRegex(regex: String, str: String): Boolean{
        return !Pattern.matches(regex, str)
    }

    fun <T> getToastError(result: Result<T>): Int {
        return FormField("TOAST").getError(result)
    }


}