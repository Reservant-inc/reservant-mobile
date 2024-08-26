package reservant_mobile.data.services

import io.ktor.http.HttpStatusCode
import reservant_mobile.data.endpoints.MenuItems
import reservant_mobile.data.endpoints.Menus
import reservant_mobile.data.endpoints.MyRestaurants
import reservant_mobile.data.endpoints.Restaurants
import reservant_mobile.data.models.dtos.RestaurantMenuDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.models.dtos.fields.Result


interface IRestaurantMenuService{
    suspend fun addMenu(menu: RestaurantMenuDTO): Result<RestaurantMenuDTO?>
    suspend fun getMenus(restaurantId:Any): Result<List<RestaurantMenuDTO>?>
    suspend fun getOwnerMenus(restaurantId:Any): Result<List<RestaurantMenuDTO>?>
    suspend fun getMenu(id: Any): Result<RestaurantMenuDTO?>
    suspend fun editMenu(id: Any, menu: RestaurantMenuDTO): Result<RestaurantMenuDTO?>
    suspend fun deleteMenu(id: Any): Result<Boolean>
    suspend fun addItemsToMenu(menuId: Any, itemsIds:List<Int>): Result<RestaurantMenuDTO?>
    suspend fun createMenuItem(menuItems: RestaurantMenuItemDTO): Result<RestaurantMenuItemDTO?>
    suspend fun getMenuItems(restaurantId:Any): Result<List<RestaurantMenuItemDTO>?>
    suspend fun getOwnerMenuItems(restaurantId:Any): Result<List<RestaurantMenuItemDTO>?>
    suspend fun getMenuItem(id:Any): Result<RestaurantMenuItemDTO?>
    suspend fun editMenuItem(menuItemId: Any, item: RestaurantMenuItemDTO): Result<RestaurantMenuItemDTO?>
    suspend fun deleteMenuItem(id: Any): Result<Boolean>
}

class RestaurantMenuService():ServiceUtil(), IRestaurantMenuService {

    override suspend fun addMenu(menu: RestaurantMenuDTO): Result<RestaurantMenuDTO?> {
        val res = api.post(Menus(), menu)
        return complexResultWrapper(res)
    }

    override suspend fun getMenus(restaurantId: Any): Result<List<RestaurantMenuDTO>?> {
        val res = api.get(Restaurants.Id.Menus(Restaurants.Id(restaurantId =restaurantId.toString())))
        return complexResultWrapper(res)
    }

    override suspend fun getOwnerMenus(restaurantId: Any): Result<List<RestaurantMenuDTO>?> {
        val res = api.get(MyRestaurants.Id.Menus(MyRestaurants.Id(restaurantId = restaurantId.toString())))
        return complexResultWrapper(res)
    }

    override suspend fun getMenu(id: Any): Result<RestaurantMenuDTO?> {
        val res = api.get(Menus.Id(id=id.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun editMenu(id: Any, menu: RestaurantMenuDTO): Result<RestaurantMenuDTO?> {
        val res = api.put(Menus.Id(id=id.toString()), menu)
        return complexResultWrapper(res)
    }

    override suspend fun deleteMenu(id: Any): Result<Boolean> {
        val res = api.delete(Menus.Id(id=id.toString()))
        return booleanResultWrapper(res, expectedCode = HttpStatusCode.NoContent)
    }

    override suspend fun addItemsToMenu(menuId: Any, itemsIds:List<Int>): Result<RestaurantMenuDTO?> {
        val newGroup: HashMap<String, List<Int>> = hashMapOf("itemIds" to itemsIds)
        val res = api.post(Menus.Id.Items(Menus.Id(id=menuId.toString())), newGroup)
        return complexResultWrapper(res)
    }

    override suspend fun createMenuItem(menuItems: RestaurantMenuItemDTO): Result<RestaurantMenuItemDTO?> {
        val res = api.post( MenuItems(), menuItems)
        return complexResultWrapper(res)
    }

    override suspend fun getMenuItems(restaurantId: Any): Result<List<RestaurantMenuItemDTO>?> {
        val res = api.get(MyRestaurants.Id.MenuItems(MyRestaurants.Id(restaurantId=restaurantId.toString())))
        return complexResultWrapper(res)
    }

    override suspend fun getOwnerMenuItems(restaurantId: Any): Result<List<RestaurantMenuItemDTO>?> {
        val res = api.get(Restaurants.Id.MenuItems(Restaurants.Id(restaurantId=restaurantId.toString())))
        return complexResultWrapper(res)
    }

    override suspend fun getMenuItem(id: Any): Result<RestaurantMenuItemDTO?> {
        val res = api.get(MenuItems.Id(id=id.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun editMenuItem(menuItemId: Any, item: RestaurantMenuItemDTO): Result<RestaurantMenuItemDTO?> {
        val res = api.put(MenuItems.Id(id=menuItemId.toString()), item)
        return complexResultWrapper(res)
    }

    override suspend fun deleteMenuItem(id: Any): Result<Boolean> {
        val res = api.delete(MenuItems.Id(id=id.toString()))
        return booleanResultWrapper(res, expectedCode = HttpStatusCode.NoContent)
    }
}