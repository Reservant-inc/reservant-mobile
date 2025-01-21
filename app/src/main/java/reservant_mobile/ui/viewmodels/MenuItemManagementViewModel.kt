package reservant_mobile.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.services.DataType
import reservant_mobile.data.services.FileService
import reservant_mobile.data.services.IRestaurantMenuService
import reservant_mobile.data.services.RestaurantMenuService
import reservant_mobile.data.utils.getFileFromUri

class MenuItemManagementViewModel(
    private val menuId: Int,
    private val restaurantId: Int,
    private val service: IRestaurantMenuService = RestaurantMenuService(),
): ReservantViewModel() {
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

    suspend fun sendPhoto(uri: String?, context: Context): String {
        val file = uri?.let { getFileFromUri(context, it.toUri()) }
        var fDto = file?.let { fileService.sendFile(DataType.PNG, it).value }
        if (fDto == null) {
            fDto = file?.let { fileService.sendFile(DataType.JPG, it).value }
        }
        return fDto?.fileName ?: ""
    }

    private suspend fun createMenuItemDTO(menuItemId: Int? = null, context: Context): RestaurantMenuItemDTO {
        return RestaurantMenuItemDTO(
            menuItemId  = menuItemId,
            name = name.value,
            restaurantId = restaurantId,
            alternateName = alternateName.value.ifEmpty { null },
            price = price.value.toDouble(),
            alcoholPercentage = alcoholPercentage.value.toDoubleOrNull(),
            photoFileName = photo.value,
            photo = photo.value
        )
    }

    suspend fun createMenuItem(context: Context){
        val menuitem = createMenuItemDTO(context = context)

        val result = service.createMenuItem(menuitem)

        //TODO z jakiegoś powodu resultFirstStep.isError zwraca true, a dalej zwraca poprawne dane
        //TODO resultFirstStep.menuitemid np to null ale nie wiem dlaczego bo zwraca dane normalnie ta koncowka
        println(result.errors)
        if(!result.isError){
            val menuItemId = result.value?.menuItemId
            val resultadd = service.addItemsToMenu(menuId, listOf(menuItemId!!))

            if(!resultadd.isError){
                fetchMenuItems()
            }
        }

    }

    suspend fun addMenuItemToMenu(context: Context){
        val menuitem = createMenuItemDTO(context = context)

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

    suspend fun editMenuItem(menuItem: RestaurantMenuItemDTO, context: Context) {
        val editedMenuItem = createMenuItemDTO(menuItem.menuItemId, context)

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

    fun isFormValid(): Boolean {
        return name.value.isNotBlank() &&
                (price.value.toDoubleOrNull() ?: 0.0 ) > 0 &&
                (alcoholPercentage.value.toDoubleOrNull() ?: 0.0 ) >= 0 &&
                isPhotoValid()
    }

    fun isPhotoValid(): Boolean {
        return true
    }
}