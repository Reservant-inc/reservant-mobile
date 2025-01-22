package reservant_mobile.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.data.models.dtos.IngredientDTO.IngredientMenuItemDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.models.dtos.UnitOfMeasurement
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.services.DataType
import reservant_mobile.data.services.IRestaurantMenuService
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.RestaurantMenuService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.utils.getFileFromUri

class MenuItemManagementViewModel(
    private val menuId: Int,
    private val restaurantId: Int,
    private val menuService: IRestaurantMenuService = RestaurantMenuService(),
    private val restaurantService: IRestaurantService = RestaurantService()
): ReservantViewModel() {
    var items by mutableStateOf<List<RestaurantMenuItemDTO>>(emptyList())

    val name: FormField = FormField(RestaurantMenuItemDTO::name.name)
    val alternateName: FormField = FormField(RestaurantMenuItemDTO::alternateName.name)
    val price: FormField = FormField(RestaurantMenuItemDTO::stringPrice.name)
    val alcoholPercentage: FormField = FormField(RestaurantMenuItemDTO::stringAlcoholPercentage.name)
    val photo: FormField = FormField(RestaurantMenuItemDTO::photoFileName.name)
    var ingredients: MutableList<String> = mutableListOf()

    var restaurantIngredients: List<IngredientDTO> = emptyList()

    init {
        viewModelScope.launch {
            fetchMenuItems()
            fetchIngredients()
        }
    }

    private suspend fun fetchMenuItems(){
        items = menuService.getMenu(menuId).value?.menuItems?.toMutableList() ?: mutableListOf()
        items.forEach{item ->
            price.value = item.price.toString()
            alcoholPercentage.value = item.alcoholPercentage.toString()
        }
    }

    private suspend fun fetchIngredients(){
        val res = restaurantService.getIngredients(restaurantId = restaurantId)

        if (!res.isError){
            restaurantIngredients = res.value.orEmpty().map {
                it.copy(amountUsed = 1.0)
            }
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
            photo = sendPhoto(photo.value, context),
            ingredients = restaurantIngredients.filter {
                ingredients.contains(it.publicName)
            }
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
            ingredients = restaurantIngredients.filter {
                ingredients.contains(it.publicName)
            }
        )
    }

    suspend fun createMenuItem(context: Context){
        val menuitem = createMenuItemDTO(context = context)

        val result = menuService.createMenuItem(menuitem)

        if(!result.isError){
            val menuItemId = result.value?.menuItemId
            val resultadd = menuService.addItemsToMenu(menuId, listOf(menuItemId!!))

            if(!resultadd.isError){
                fetchMenuItems()
            }
        }

    }

    suspend fun deleteMenuItem(id: Int){
        val result = menuService.deleteMenuItem(id)

        if (!result.isError){
            fetchMenuItems()
        }
    }

    suspend fun editMenuItem(menuItem: RestaurantMenuItemDTO) {
        val editedMenuItem = putMenuItemDTO(menuItem.menuItemId)

        val result = menuService.editMenuItem(menuItem.menuItemId!!, editedMenuItem)

        if (!result.isError){
            fetchMenuItems()
        }

    }

    suspend fun fetchIngredientsForMenuItem(id: Int){
        val res = menuService.getMenuItem(id)

        if (!res.isError){
            ingredients = res.value!!.ingredients?.map { it.publicName.orEmpty() }
                .orEmpty()
                .toMutableList()
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

    fun onIngredientAdded(ingredient: String) {
        ingredients += ingredient
    }

    fun onIngredientRemoved(ingredient: String){
        ingredients -= ingredient
    }
}