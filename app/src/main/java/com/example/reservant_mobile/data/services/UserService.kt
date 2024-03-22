package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RegisterUserDTO
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO
import com.example.reservant_mobile.ui.constants.Endpoints


interface IUserService{
    suspend fun registerUser(user: RegisterUserDTO): List<Int>
    suspend fun loginUser(credentials: LoginCredentialsDTO): Boolean
}

class UserService(private var api: APIService = APIServiceImpl()) : IUserService {

    /**
     * @return -1 if everything is ok. Otherwise id of error string
     */
    override suspend fun registerUser(user: RegisterUserDTO): List<Int> {
        val res = api.post(user, Endpoints.REGISTER_CUSTOMER) ?: return listOf(R.string.error_connection_server)
        if (res.status.value == 200) return listOf(-1)
//        TODO: return string ids based on ErrCode
//        val j = JSONObject(res.body() as String)
//        if(j.has("ErrCode")) {}
        return listOf(R.string.error_register_username_taken)
    }

    override suspend fun loginUser(credentials: LoginCredentialsDTO): Boolean {
        val res = api.post(credentials, Endpoints.LOGIN) ?: return false
        return res.status.value == 200
    }



}