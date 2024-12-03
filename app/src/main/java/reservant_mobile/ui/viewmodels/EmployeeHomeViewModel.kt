package reservant_mobile.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import reservant_mobile.data.constants.PrefsKeys
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.LocalDataService
import reservant_mobile.data.services.RestaurantService

class EmployeeHomeViewModel(
    private val localDataService: LocalDataService = LocalDataService(),
    private val restaurantService: IRestaurantService = RestaurantService(),
    ): ReservantViewModel() {

    var selectedRestaurant: RestaurantDTO? by mutableStateOf(null)
    var restaurants: List<RestaurantDTO> by mutableStateOf(listOf())
    var isLoading: Boolean by mutableStateOf(false)
    var isError: Boolean by mutableStateOf(false)



    suspend fun getEmployeeRestaurants(){
        isLoading = true
        val res = restaurantService.getRestaurant(5);
        if(!res.isError){
            restaurants = listOf(res.value!!)
        }
        isLoading = false
        isError = res.isError
    }

    suspend fun selectRestaurant(restaurant: RestaurantDTO){
        selectedRestaurant = restaurant
        localDataService.saveData(PrefsKeys.EMPLOYEE_CURRENT_RESTAURANT, restaurant.restaurantId.toString())
    }

    suspend fun findSelectedRestaurants(){
        isLoading = true
        val selectedId = localDataService.getData(PrefsKeys.EMPLOYEE_CURRENT_RESTAURANT)

        if(selectedId.isEmpty()){
            isLoading = false
            return
        }

        if(restaurants.isEmpty()){
            isLoading = false
            return
        }

        selectedRestaurant = restaurants.find { it.restaurantId == selectedId.toInt() }
        isLoading = false
    }

    suspend fun getRestaurantLogo(restaurant: RestaurantDTO): Bitmap? {
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

}