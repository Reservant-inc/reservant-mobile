package reservant_mobile.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.RestaurantMenuDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.EventService
import reservant_mobile.data.services.IEventService
import reservant_mobile.data.services.IRestaurantMenuService
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.RestaurantMenuService
import reservant_mobile.data.services.RestaurantService

class RestaurantDetailViewModel(
    private var restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService(),
    private val eventsService: IEventService = EventService(),
    private val menuService: IRestaurantMenuService = RestaurantMenuService(),
) : ReservantViewModel() {

    var resultRestaurant: Result<RestaurantDTO?> by mutableStateOf(Result(isError = false, value=null))
    var resultMenus: Result<List<RestaurantMenuDTO>?> by mutableStateOf(Result(isError = false, value=null))

    var restaurant: RestaurantDTO? by mutableStateOf(null)
    var menus: List<RestaurantMenuDTO>? by mutableStateOf(emptyList())
    var currentMenu: RestaurantMenuDTO? by mutableStateOf(null)
    var isLoading: Boolean by mutableStateOf(false)
    var isGalleryLoading: Boolean by mutableStateOf(false)

    private val _eventsState = MutableStateFlow<PagingData<EventDTO>>(PagingData.empty())
    val events: StateFlow<PagingData<EventDTO>> = _eventsState.asStateFlow()



    init {
        viewModelScope.launch {
            loadRestaurantAndMenus(restaurantId)
            loadEvents()
        }
    }

    private suspend fun loadRestaurantAndMenus(id: Int) {
        isLoading = true
        val restaurantLoaded = loadRestaurant(id)
        if (restaurantLoaded) {
            loadMenus(id)
            menus?.firstOrNull()?.menuId?.let { loadFullMenu(it) }
        }
        isLoading = false
    }

    suspend fun loadRestaurant(id: Int): Boolean {
        isLoading = true

        if (id != restaurantId) {
            restaurantId = id
        }
        resultRestaurant = restaurantService.getRestaurant(id)
        isLoading = false

        if (resultRestaurant.isError) {
            return false
        }
        restaurant = resultRestaurant.value
        return true
    }

    suspend fun loadEvents(){
        val res = eventsService.getEvents(
            restaurantId = restaurantId
        )

        if(res.isError || res.value == null){
            throw Exception()
        } else {
            res.value.cachedIn(viewModelScope).collect {
                _eventsState.value = it
            }
        }

    }

    suspend fun getPhotos(urls: List<String>, limit: Int = urls.size, withLoading:Boolean = true): List<Bitmap>{
        if(withLoading) isGalleryLoading = true
        val photos = urls.take(limit).mapNotNull { url -> getPhoto(url) }
        if(withLoading) isGalleryLoading = false
        return photos;
    }

    suspend fun getPhoto(photoStr: String): Bitmap? {
        val result = fileService.getImage(photoStr)
        if (!result.isError){
            return result.value!!
        }
        return null
    }

    private suspend fun loadMenus(id: Int) {
        resultMenus = menuService.getMenus(id)
        if (!resultMenus.isError) {
            menus = resultMenus.value
        }
    }

    suspend fun loadFullMenu(menuId: Int) {
        val result = menuService.getMenu(menuId)
        if (!result.isError) {
            currentMenu = result.value
        }
    }

    fun getToastError(): Int {
        return getToastError(resultRestaurant)
    }
}