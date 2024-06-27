package reservant_mobile.data.services

import com.example.reservant_mobile.R
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import reservant_mobile.data.endpoints.Auth
import reservant_mobile.data.models.dtos.LoginCredentialsDTO
import reservant_mobile.data.models.dtos.RegisterUserDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.models.dtos.fields.Result


interface IUserService{
    suspend fun isLoginUnique(login: String): Boolean
    suspend fun registerUser(user: RegisterUserDTO): Result<Boolean>
    suspend fun loginUser(credentials: LoginCredentialsDTO): Result<Boolean>
    suspend fun logoutUser()
    suspend fun refreshToken(): Boolean

}

class UserService(private var api: APIService = APIService()) : IUserService {
    private val localBearer = LocalBearerService()
    object User {
        lateinit var userId: String
        lateinit var login: String
        lateinit var firstName: String
        lateinit var lastName: String
        lateinit var roles:List<String>

        fun clearData(){
            userId = ""
            login = ""
            firstName = ""
            lastName = ""
        }
    }
    
    private suspend fun wrapUser(u: UserDTO){
        u.userId?.let { User.userId = it }
        User.login = u.login!!
        User.firstName = u.firstName
        User.lastName = u.lastName
        User.roles = u.roles!!
        localBearer.saveBearerToken(u.token!!)
    }


    override suspend fun isLoginUnique(login: String): Boolean {
        val res = api.get(Auth.IsUniqueLogin(login=login)).value
            ?: return false

        return if (res.status == HttpStatusCode.OK){
            res.body<Boolean>()
        } else {
            false
        }
    }

    override suspend fun registerUser(user: RegisterUserDTO): Result<Boolean> {
        //return errors in toast when connection error
        val res = api.post(Auth.RegisterCustomer(), user)
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        //return true if successful
        if (res.value!!.status == HttpStatusCode.OK) return Result(isError = false, value = true)

        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun loginUser(credentials: LoginCredentialsDTO): Result<Boolean> {
        //return errors in toast when connection error
        val res = api.post(Auth.Login(), credentials)
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        //return true if successful and save token
        if(res.value!!.status == HttpStatusCode.OK){
            return try {
                val user: UserDTO = res.value.body()
                wrapUser(user)
                Result(isError = false, value = true)
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = false)
            }
        }
        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_login_wrong_credentials)), false)
    }

    override suspend fun logoutUser() {
        User.clearData()
        api.clearToken()
    }

    override suspend fun refreshToken(): Boolean {
         val res = api.post(Auth.RefreshToken(), "")

         if(res.isError)
             return false

         return if(res.value!!.status == HttpStatusCode.OK){
             try{
                 val user: UserDTO = res.value.body()
                 wrapUser(user)
                 true
             }
             catch (e: Exception) {
                false
             }
         }
         else false
    }
}