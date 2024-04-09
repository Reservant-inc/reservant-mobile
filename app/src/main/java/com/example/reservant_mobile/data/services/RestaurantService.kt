package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RegisterRestaurantDTO
import com.example.reservant_mobile.ui.constants.Endpoints

class RestaurantService(private var api: APIService = APIServiceImpl()) {

    suspend fun registerRestaurant(restaurant: RegisterRestaurantDTO): List<Int> {
        val res = api.post(restaurant, Endpoints.MY_RESTAURANTS) ?: return listOf(R.string.error_connection_server)
        if (res.status.value == 200) return listOf(-1)
        if (res.status.value == 401) return listOf(R.string.error_unauthorized_access)

//        TODO: return string ids based on ErrCode
//        val j = JSONObject(res.body() as String)
//        if(j.has("ErrCode")) {}
        return listOf(R.string.error_unknown)
    }

}