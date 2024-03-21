package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RegisterUserDTO
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO


interface IUserService{
    suspend fun registerUser(user: RegisterUserDTO): Int
    suspend fun loginUser(credentials: LoginCredentialsDTO): Boolean
}

class UserService(private var api: APIService = APIServiceImpl()) : IUserService {

    /**
     * @return -1 if everything is ok. Otherwise id of error string
     */
    override suspend fun registerUser(user: RegisterUserDTO): Int {
//        TODO("point endpoint to /res/.../endpoints.xml")
        val res = api.post(user,"/auth/register-customer") ?: return R.string.error_connection_server
        if (res.status.value == 200) return -1
//        TODO: return string ids based on ErrCode
//        val j = JSONObject(res.body() as String)
//        if(j.has("ErrCode")) {}
        return R.string.error_register_username_taken
    }

    override suspend fun loginUser(credentials: LoginCredentialsDTO): Boolean {
//        TODO("point endpoint to /res/.../endpoints.xml")
        val res = api.post(credentials,"/auth/login") ?: return false
        return res.status.value == 200
    }

}