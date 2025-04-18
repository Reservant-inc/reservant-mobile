package reservant_mobile.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.TableBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.reservant_mobile.R
import reservant_mobile.data.constants.PrefsKeys
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.models.dtos.EmploymentDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.LocalDataService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.services.UserService
import reservant_mobile.ui.activities.EmpMenuOption
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.RestaurantRoutes

class EmployeeHomeViewModel(
    val localDataService: LocalDataService = LocalDataService(),
    private val restaurantService: IRestaurantService = RestaurantService(),
    private val userService: IUserService = UserService()
): ReservantViewModel() {

    var selectedEmployment: EmploymentDTO? by mutableStateOf(null)
    var employments: List<EmploymentDTO> by mutableStateOf(listOf())
    var isLoading: Boolean by mutableStateOf(false)
    var isError: Boolean by mutableStateOf(false)


    suspend fun getEmployeeRestaurants(){
        isLoading = true
        val res = userService.getUserEmployments(false)
        if(!res.isError){
            employments = res.value!!
        }
        isLoading = false
        isError = res.isError
    }

    suspend fun selectRestaurant(employment: EmploymentDTO){
        selectedEmployment = employment
        localDataService.saveData(PrefsKeys.EMPLOYEE_CURRENT_RESTAURANT, employment.restaurant!!.restaurantId.toString())
    }

    suspend fun findSelectedRestaurants(){
        isLoading = true
        val selectedId = localDataService.getData(PrefsKeys.EMPLOYEE_CURRENT_RESTAURANT)

        if(selectedId.isEmpty()){
            isLoading = false
            return
        }

        if(employments.isEmpty()){
            isLoading = false
            return
        }

        selectedEmployment = employments.find { it.restaurant!!.restaurantId == selectedId.toInt() }
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

    fun isBackdoorEmp(): Boolean {
        return selectedEmployment?.isBackdoorEmployee ?: false
    }

}