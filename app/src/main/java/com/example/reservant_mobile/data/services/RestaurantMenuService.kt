package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RegisterRestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.ui.constants.Endpoints
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode


interface IRestaurantMenuService{
    suspend fun addMenu(restaurantId: Any, menu: RestaurantMenuDTO): Result<RestaurantMenuDTO?>
    suspend fun getMenus(restaurantId:Any): Result<List<RestaurantMenuDTO>?>
    suspend fun getMenu(restaurantId:Any, menuId: Any): Result<RestaurantMenuDTO?>
    suspend fun createMenuItems(restaurantId:Any, menuItems: List<RestaurantMenuItemDTO>): Result<List<RestaurantMenuItemDTO>?>
    suspend fun getMenuItems(restaurantId:Any): Result<List<RestaurantMenuItemDTO>?>
    suspend fun getMenuItem(restaurantId:Any, itemId:Any): Result<RestaurantMenuItemDTO?>

}
class RestaurantMenuService(private var api: APIService = APIServiceImpl()): IRestaurantMenuService {
    override suspend fun addMenu(restaurantId: Any, menu: RestaurantMenuDTO): Result<RestaurantMenuDTO?>{
        val res = api.post(menu, Endpoints.RESTAURANT_MENUS(restaurantId.toString()))

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

    override suspend fun getMenus(restaurantId: Any): Result<List<RestaurantMenuDTO>?> {
        val res = api.get(Endpoints.RESTAURANT_MENUS(restaurantId.toString()))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value =  res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun getMenu(restaurantId: Any, menuId: Any): Result<RestaurantMenuDTO?> {
        val res = api.get(Endpoints.RESTAURANT_MENU(restaurantId.toString(), menuId.toString()))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value =  res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun createMenuItems(restaurantId: Any, menuItems: List<RestaurantMenuItemDTO>): Result<List<RestaurantMenuItemDTO>?> {
        val res = api.post(menuItems, Endpoints.RESTAURANT_MENU_ITEMS(restaurantId.toString()))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.Created){
            return try {
                Result(isError = false, value =  res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }


    override suspend fun getMenuItems(restaurantId: Any): Result<List<RestaurantMenuItemDTO>?> {
        val res = api.get(Endpoints.RESTAURANT_MENU_ITEMS(restaurantId.toString()))

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

    override suspend fun getMenuItem(restaurantId: Any, itemId: Any): Result<RestaurantMenuItemDTO?> {
        val res = api.get(Endpoints.RESTAURANT_MENU_ITEM(restaurantId.toString(), itemId.toString()))

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

}