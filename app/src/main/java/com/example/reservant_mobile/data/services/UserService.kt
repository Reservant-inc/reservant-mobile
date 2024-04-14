package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO
import com.example.reservant_mobile.data.models.dtos.LoginResponseDTO
import com.example.reservant_mobile.data.models.dtos.RegisterUserDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.ui.constants.Endpoints
import io.ktor.client.call.body
import org.json.JSONObject


interface IUserService{
    suspend fun isLoginUnique(login: String): Boolean
    suspend fun registerUser(user: RegisterUserDTO): List<Int>
    suspend fun loginUser(credentials: LoginCredentialsDTO): Int
    suspend fun refreshToken(): Boolean
}

class UserService(private var api: APIService = APIServiceImpl()) : IUserService {
    override suspend fun isLoginUnique(login: String): Boolean {
        val res = api.post(login, Endpoints.LOGIN_UNIQUE)
            ?: return true
        return res.status.value == 200
    }

    override suspend fun registerUser(user: RegisterUserDTO): Result<Boolean> {
        //return errors in toast when connection error
        val res = api.post(user, Endpoints.REGISTER_CUSTOMER)
            ?: return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), false)

        //return true if successful
        if (res.status.value == 200) return Result(isError = false, value = true)

        //return errors

        //TODO: JSON errors parse
        //val j = JSONObject(res.body() as String).getJSONObject("errors")

        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun loginUser(credentials: LoginCredentialsDTO): Result<Boolean> {
        //return errors in toast when connection error
        val res = api.post(credentials, Endpoints.LOGIN)
            ?: return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), false)

        //return true if successful and save token
        if(res.status.value == 200){
            return try {
                val user: LoginResponseDTO = res.body()
                LocalBearerService().saveBearerToken(user.token)
                Result(isError = false, value = true)
            }
            catch (e: Exception){
                println("[LOGIN PARSING ERROR]: "+e.message)
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = false)
            }
        }

        //return errors
        //val j = JSONObject(res.body() as String)

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_login_wrong_credentials)), false)
    }
     override suspend fun refreshToken(): Boolean {
         if(LocalBearerService().getBearerToken().isEmpty()) return false
         val res = api.get("/auth/refresh-token") ?: return false
         return res.status.value == 200
    }



}