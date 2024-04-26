package com.example.reservant_mobile.data.services

import androidx.collection.emptyIntSet
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO
import com.example.reservant_mobile.data.models.dtos.UserDTO
import com.example.reservant_mobile.data.models.dtos.RegisterUserDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.ui.constants.Endpoints
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode


interface IUserService{
    suspend fun isLoginUnique(login: String): Boolean
    suspend fun registerUser(user: RegisterUserDTO): Result<Boolean>
    suspend fun loginUser(credentials: LoginCredentialsDTO): Result<Boolean>
    suspend fun refreshToken(): Boolean

}

class UserService(private var api: APIService = APIServiceImpl()) : IUserService {
    override suspend fun isLoginUnique(login: String): Boolean {
        val res = api.post(login, Endpoints.LOGIN_UNIQUE).value
            ?: return true

        return if (res.status == HttpStatusCode.OK){
            res.body<Boolean>()
        } else {
            true
        }
    }

    override suspend fun registerUser(user: RegisterUserDTO): Result<Boolean> {
        //return errors in toast when connection error
        val res = api.post(user, Endpoints.REGISTER_CUSTOMER)
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        //return true if successful
        if (res.value!!.status == HttpStatusCode.OK) return Result(isError = false, value = true)

        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun loginUser(credentials: LoginCredentialsDTO): Result<Boolean> {
        //return errors in toast when connection error
        val res = api.post(credentials, Endpoints.REGISTER_CUSTOMER)
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        //return true if successful and save token
        if(res.value!!.status == HttpStatusCode.OK){
            return try {
                val user: UserDTO = res.value.body()
                LocalBearerService().saveBearerToken(user.token!!)
                Result(isError = false, value = true)
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = false)
            }
        }
        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_login_wrong_credentials)), false)
    }
     override suspend fun refreshToken(): Boolean {
         val res = api.post("",Endpoints.REFRESH_ACCESS_TOKEN)

         if(res.isError)
             return false

         return if(res.value!!.status == HttpStatusCode.OK){
             try{
                 val user: UserDTO = res.value.body()
                 LocalBearerService().saveBearerToken(user.token!!)
                 true
             }
             catch (e: Exception) {
                false
             }
         }
         else false
    }



}