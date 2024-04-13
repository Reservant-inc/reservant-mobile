package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RegisterUserDTO
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO
import com.example.reservant_mobile.data.models.dtos.LoginResponseDTO
import com.example.reservant_mobile.ui.constants.Endpoints
import io.ktor.client.call.body


interface IUserService{
    suspend fun isLoginUnique(login: String): Boolean
    suspend fun registerUser(user: RegisterUserDTO): List<Int>
    suspend fun loginUser(credentials: LoginCredentialsDTO): Int
    suspend fun test(): Int
}

class UserService(private var api: APIService = APIServiceImpl()) : IUserService {
    override suspend fun isLoginUnique(login: String): Boolean {
        val res = api.post(login, Endpoints.REGISTER_CUSTOMER)
            ?: return true
        return res.status.value == 200
    }

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

    override suspend fun loginUser(credentials: LoginCredentialsDTO): Int {
        val res = api.post(credentials, Endpoints.LOGIN) ?: return R.string.error_connection_server

        if(res.status.value != 200)
            return R.string.error_login_wrong_credentials

        return try {
            val user: LoginResponseDTO = res.body()
            LocalBearerService().saveBearerToken(user.token)

            -1
        }
        catch (e: Exception){
            println("[LOGIN PARSING ERROR]: "+e.message)
            R.string.error_unknown
        }
    }
     override suspend fun test(): Int {
        val res = api.get("/test/restaurant-owner-only") ?: return R.string.error_connection_server

         return -1

    }



}