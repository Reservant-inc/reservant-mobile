package reservant_mobile.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import reservant_mobile.data.endpoints.Ingredients
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.services.DataType
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
    var ingredients: List<IngredientDTO> = emptyList()

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

    private suspend fun createMenuItemDTO(context: Context): RestaurantMenuItemDTO {
        return RestaurantMenuItemDTO(
            name = name.value,
            restaurantId = restaurantId,
            alternateName = alternateName.value.ifEmpty { null },
            price = price.value.toDouble(),
            alcoholPercentage = alcoholPercentage.value.toDoubleOrNull(),
            photoFileName = sendPhoto(photo.value, context),
            ingredients = ingredients
        )
    }

    private fun putMenuItemDTO(menuItemId: Int? = null): RestaurantMenuItemDTO {
        return RestaurantMenuItemDTO(
            menuItemId = menuItemId,
            name = name.value,
            restaurantId = restaurantId,
            alternateName = alternateName.value.ifEmpty { null },
            price = price.value.toDouble(),
            alcoholPercentage = alcoholPercentage.value.toDoubleOrNull(),
            photo = photo.value,
            ingredients = ingredients
        )
    }

    suspend fun createMenuItem(context: Context){
        val menuitem = createMenuItemDTO(context = context)

        val result = service.createMenuItem(menuitem)

        if(!result.isError){
            val menuItemId = result.value?.menuItemId
            val resultadd = service.addItemsToMenu(menuId, listOf(menuItemId!!))

            if(!resultadd.isError){
                fetchMenuItems()
            }
        }

    }

    suspend fun deleteMenuItem(id: Int){
        val result = service.deleteMenuItem(id)

        if (!result.isError){
            fetchMenuItems()
        }
    }

    suspend fun editMenuItem(menuItem: RestaurantMenuItemDTO) {
        val editedMenuItem = putMenuItemDTO(menuItem.menuItemId)

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

    private fun isPhotoValid(): Boolean {
        return true
    }

    fun assignIngredients(ingredients: List<IngredientDTO>?){
        this.ingredients = ingredients ?: emptyList()
    }
}