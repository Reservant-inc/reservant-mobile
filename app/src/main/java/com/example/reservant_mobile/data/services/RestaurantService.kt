package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RegisterRestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.ui.constants.Endpoints
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

interface IRestaurantService{
    suspend fun registerRestaurant(restaurant: RegisterRestaurantDTO): Result<Boolean>
    suspend fun getRestaurants():Result<List<RestaurantDTO>?>
    suspend fun getRestaurant(id:Any): Result<RestaurantDTO?>
    suspend fun editRestaurant(id: Any, restaurant: RestaurantDTO): Result<Boolean>
    suspend fun deleteRestaurant(id: Int): Result<Boolean>

    }

class RestaurantService(private var api: APIService = APIServiceImpl()): IRestaurantService {

    override suspend fun registerRestaurant(restaurant: RegisterRestaurantDTO): Result<Boolean> {
        val res = api.post(restaurant, Endpoints.MY_RESTAURANTS) ?:
            return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), false)

        if (res.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = false)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)

    }

    override suspend fun getRestaurants():Result<List<RestaurantDTO>?>  {
        val res = api.get(Endpoints.MY_RESTAURANTS) ?:
            return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), null)


        if (res.status == HttpStatusCode.OK){
            return try {
                val j:List<RestaurantDTO> = res.body()
                Result(isError = false, value = j)
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }
    override suspend fun getRestaurant(id:Any): Result<RestaurantDTO?>  {
        val res = api.get(Endpoints.MY_RESTAURANT(id.toString())) ?:
            return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), null)


        if (res.status == HttpStatusCode.OK){
            return try {
                val j:RestaurantDTO = res.body()
                Result(isError = false, value = j)
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)

    }

    override suspend fun editRestaurant(id: Any, restaurant: RestaurantDTO): Result<Boolean>  {
//        TODO: Implement edit (add put)
        val res = api.post( restaurant ,Endpoints.MY_RESTAURANT(id.toString())) ?:
        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), false)


        if (res.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = false)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun deleteRestaurant(id: Int): Result<Boolean>  {
//        TODO: Implement delete
        val res = api.get(Endpoints.MY_RESTAURANT(id.toString())) ?:
        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), false)


        if (res.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = false)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }

}