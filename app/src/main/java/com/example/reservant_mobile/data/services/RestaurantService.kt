package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RegisterRestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.ui.constants.Endpoints
import io.ktor.client.call.body

class RestaurantService(private var api: APIService = APIServiceImpl()) {

    suspend fun registerRestaurant(restaurant: RegisterRestaurantDTO): List<Int> {
        val res = api.post(restaurant, Endpoints.MY_RESTAURANTS) ?: return listOf(R.string.error_connection_server)
        if (res.status.value == 200) return listOf(-1)
        if (res.status.value == 401) return listOf(R.string.error_unauthorized_access)

        return listOf(R.string.error_unknown)
    }

    suspend fun getRestaurants():String {
        val res = api.get(Endpoints.MY_RESTAURANTS)?: return "error_connection_server"

        if (res.status.value == 200){
            val j:List<RestaurantDTO> = res.body()
            return j.toString()
        }
        if (res.status.value == 401) return "unauthorized"

        return "error_unknown"

    }
    suspend fun getRestaurant(id:Any): String {
        val res = api.get(Endpoints.MY_RESTAURANT.replace("{id}",id.toString())) ?: return "error_connection_server"

        if (res.status.value == 200){
            val j:RestaurantDTO = res.body()
            return j.toString()
        }
        if (res.status.value == 401) return "unauthorized"

        return "error_unknown"

    }

    suspend fun editRestaurant(restaurant: RegisterRestaurantDTO): List<Int> {
//        TODO: Implement edit
        /*val res = api.post(restaurant, Endpoints.MY_RESTAURANTS) ?: return listOf(R.string.error_connection_server)
        if (res.status.value == 200) return listOf(-1)
        if (res.status.value == 401) return listOf(R.string.error_unauthorized_access)

        return listOf(R.string.error_unknown)*/
        return listOf(0)
    }

    suspend fun deleteRestaurant(id: Int): List<Int> {
//        TODO: Implement delete
        /*val res = api.post(restaurant, Endpoints.MY_RESTAURANTS) ?: return listOf(R.string.error_connection_server)
        if (res.status.value == 200) return listOf(-1)
        if (res.status.value == 401) return listOf(R.string.error_unauthorized_access)

        return listOf(R.string.error_unknown)*/
        return listOf(0)
    }

}