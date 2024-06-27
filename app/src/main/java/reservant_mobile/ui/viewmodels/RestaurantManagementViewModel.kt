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


class RestaurantManagementViewModel(private val restaurantService: IRestaurantService = RestaurantService()) : ViewModel() {

    var groups: List<RestaurantGroupDTO>? by mutableStateOf(listOf())
    var selectedRestaurant: RestaurantDTO? by mutableStateOf(null)
    var selectedRestaurantLogo: Bitmap? by mutableStateOf(null)
    var isLoading: Boolean by mutableStateOf(false)


    private val fileService = FileService()


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
        val group =  restaurantService.getGroup(groupId).value
        isLoading = false
        return group
    }

    suspend fun getPhoto(restaurant: RestaurantDTO): Bitmap? {
        if(restaurant.logo == null)
            return null
        isLoading = true
        val photoString = restaurant.logo.substringAfter("uploads/")
        val result = fileService.getImage(photoString)
        isLoading = false
        if (!result.isError){
            return result.value!!
        }
        return null
    }


    suspend fun getSingleRestaurant(id: Int): RestaurantDTO? {
        return restaurantService.getRestaurant(id).value
    }

    suspend fun deleteRestaurant(id: Int) {
        restaurantService.deleteRestaurant(id)
        loadGroups() // Reloading groups
    }

}