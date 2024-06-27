package reservant_mobile.data.services

import com.example.reservant_mobile.R
import reservant_mobile.data.endpoints.MenuItems
import reservant_mobile.data.endpoints.Menus
import reservant_mobile.data.endpoints.MyRestaurants
import reservant_mobile.data.models.dtos.RestaurantMenuDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.models.dtos.fields.Result
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode


interface IRestaurantMenuService{
    suspend fun addMenu(menu: RestaurantMenuDTO): Result<RestaurantMenuDTO?>
    suspend fun getMenus(restaurantId:Any): Result<List<RestaurantMenuDTO>?>
    suspend fun getMenu(id: Any): Result<RestaurantMenuDTO?>
    suspend fun editMenu(id: Any, menu: RestaurantMenuDTO): Result<RestaurantMenuDTO?>
    suspend fun deleteMenu(id: Any): Result<Boolean>
    suspend fun addItemsToMenu(menuId: Any, itemsIds:List<Int>): Result<RestaurantMenuDTO?>
    suspend fun createMenuItem(menuItems: RestaurantMenuItemDTO): Result<RestaurantMenuItemDTO?>
    suspend fun getMenuItems(restaurantId:Any): Result<List<RestaurantMenuItemDTO>?>
    suspend fun getMenuItem(id:Any): Result<RestaurantMenuItemDTO?>
    suspend fun editMenuItem(menuItemId: Any, item: RestaurantMenuItemDTO): Result<RestaurantMenuItemDTO?>
    suspend fun deleteMenuItem(id: Any): Result<Boolean>
}

class RestaurantMenuService(private var api: APIService = APIService()): IRestaurantMenuService {
    private suspend inline fun <reified T> resultWrapper(res: Result<HttpResponse?>): Result<T?> {
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun addMenu(menu: RestaurantMenuDTO): Result<RestaurantMenuDTO?> {
        val res = api.post(Menus(), menu)
        return resultWrapper(res)
    }

    override suspend fun getMenus(restaurantId: Any): Result<List<RestaurantMenuDTO>?> {
        val res = api.get(MyRestaurants.Id.Menus(MyRestaurants.Id(id=restaurantId.toString())))
        return resultWrapper(res)
    }

    override suspend fun getMenu(id: Any): Result<RestaurantMenuDTO?> {
        val res = api.get(Menus.Id(id=id.toString()))
        return resultWrapper(res)
    }

    override suspend fun editMenu(id: Any, menu: RestaurantMenuDTO): Result<RestaurantMenuDTO?> {
        val res = api.put(Menus.Id(id=id.toString()), menu)
        return resultWrapper(res)
    }

    override suspend fun deleteMenu(id: Any): Result<Boolean> {
        val res = api.delete(Menus.Id(id=id.toString()))
        
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        if (res.value!!.status == HttpStatusCode.NoContent)
            return Result(isError = false, value = true)

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun addItemsToMenu(menuId: Any, itemsIds:List<Int>): Result<RestaurantMenuDTO?> {
        val newGroup: HashMap<String, List<Int>> = hashMapOf("itemIds" to itemsIds)
        val res = api.post(Menus.Id.Items(Menus.Id(id=menuId.toString())), newGroup)
        return resultWrapper(res)

    }

    override suspend fun createMenuItem(menuItems: RestaurantMenuItemDTO): Result<RestaurantMenuItemDTO?> {
        val res = api.post( MenuItems(), menuItems)
        return resultWrapper(res)
    }


    override suspend fun getMenuItems(restaurantId: Any): Result<List<RestaurantMenuItemDTO>?> {
        val res = api.get(MyRestaurants.Id.MenuItems(MyRestaurants.Id(id=restaurantId.toString())))
        return resultWrapper(res)
    }

    override suspend fun getMenuItem(id: Any): Result<RestaurantMenuItemDTO?> {
        val res = api.get(MenuItems.Id(id=id.toString()))
        return resultWrapper(res)
    }

    override suspend fun editMenuItem(menuItemId: Any, item: RestaurantMenuItemDTO): Result<RestaurantMenuItemDTO?> {
        val res = api.put(MenuItems.Id(id=menuItemId.toString()), item)
        return resultWrapper(res)

    }

    override suspend fun deleteMenuItem(id: Any): Result<Boolean> {
        val res = api.delete(MenuItems.Id(id=id.toString()))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)
        println("TEST"+res.value!!.status)
        if (res.value!!.status == HttpStatusCode.NoContent)
            return Result(isError = false, value = true)

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)    }
}