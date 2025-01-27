package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.Regex
import reservant_mobile.data.models.dtos.RestaurantMenuDTO
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.IRestaurantMenuService
import reservant_mobile.data.services.RestaurantMenuService
import reservant_mobile.data.utils.formatToDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MenuManagementViewModel(
    private val restaurantId: Int,
    private val service: IRestaurantMenuService = RestaurantMenuService(),
): ReservantViewModel() {

    var menus by mutableStateOf<List<RestaurantMenuDTO>>(emptyList())

    val menuTypes = listOf("Food", "Alcohol")

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

    private fun createMenuDTO(menuId: Int? = null): RestaurantMenuDTO {
        return RestaurantMenuDTO(
            menuId = menuId,
            name = name.value,
            restaurantId = restaurantId,
            alternateName = alternateName.value.ifEmpty { null },
            menuType = menuType.value,
            dateFrom = dateFrom.value,
            dateUntil = dateUntil.value.ifEmpty { null },
            //photo = photo.value
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

        val editedMenu = createMenuDTO(menu.menuId).copy(menuItemsId = menu.menuItemsId)

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
        //photo.value = ""
    }

    fun isNameInvalid(): Boolean{
        return name.value.isBlank()
    }

    fun isAltNameInvalid(): Boolean{
        return isInvalidWithRegex(Regex.NAME_REG, alternateName.value) && alternateName.value.isNotBlank()
    }

    fun isMenuTypeInvalid(): Boolean {
        return menuType.value.isBlank()
    }

    fun isDateUntilInvalid(): Boolean {
        if (dateUntil.value.isNotBlank()){
            val dateToDT = LocalDate.parse(dateUntil.value, DateTimeFormatter.ISO_DATE)
            return dateToDT <= LocalDate.now()
        }

        return false
    }

    fun areDatesInvalid(): Boolean {
        if (dateFrom.value.isNotBlank() && dateUntil.value.isNotBlank()){
            val dateFromDT = LocalDate.parse(dateFrom.value, DateTimeFormatter.ISO_DATE)
            val dateToDT = LocalDate.parse(dateUntil.value, DateTimeFormatter.ISO_DATE)

            return dateFromDT > dateToDT
        }

        return false
    }

    fun isMenuValid(): Boolean {
        return !isNameInvalid() &&
                !isAltNameInvalid() &&
                !isMenuTypeInvalid() &&
                !areDatesInvalid() &&
                !isDateUntilInvalid()
    }


}