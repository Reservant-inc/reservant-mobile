package reservant_mobile.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.RestaurantGroupDTO
import reservant_mobile.data.services.FileService
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.RestaurantService


class RestaurantManagementViewModel(
    private val restaurantService: IRestaurantService = RestaurantService()
) : ReservantViewModel() {

    var groups: List<RestaurantGroupDTO>? by mutableStateOf(listOf())
    var isGroupSelected: Boolean by mutableStateOf(false)
    var selectedRestaurant: RestaurantDTO? by mutableStateOf(null)
    var selectedGroup: RestaurantGroupDTO? by mutableStateOf(null)
    var selectedRestaurantLogo: Bitmap? by mutableStateOf(null)
    var newGroupName: String by mutableStateOf("")
    var isLoading: Boolean by mutableStateOf(false)


    init {
        viewModelScope.launch {
            loadGroups()
        }
    }

    private suspend fun loadGroups(){
        isLoading = true
        groups = restaurantService.getGroups().value;
        isLoading = false
    }

    suspend fun getGroup(groupId: Int): RestaurantGroupDTO? {
        isLoading = true
        selectedGroup =  restaurantService.getGroup(groupId).value
        isGroupSelected = true
        isLoading = false
        return selectedGroup
    }

    suspend fun editGroupName(newGroupName: String): Boolean{

        val result = restaurantService.editGroup(
            selectedGroup!!.restaurantGroupId.toString(),
            newGroupName
        )

        if(!result.isError){
            loadGroups()
            return true
        }
        return false
    }

    suspend fun deleteGroup(groupId: Int): Boolean {
        val result = restaurantService.deleteGroup(groupId)

        if(!result.isError){
            loadGroups()
        }

        return !result.isError
    }

    suspend fun getPhoto(restaurant: RestaurantDTO): Bitmap? {
        if(restaurant.logo == null)
            return null
        isLoading = true
        val result = fileService.getImage(restaurant.logo)
        isLoading = false
        if (!result.isError){
            return result.value!!
        }
        return null
    }


    suspend fun deleteRestaurant(id: Int) {
        restaurantService.deleteRestaurant(id)
        loadGroups() // Reloading groups
    }

    fun isGroupNameInvalid(): Boolean{
        return newGroupName.isBlank()
    }

}