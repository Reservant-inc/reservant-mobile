package reservant_mobile.data.services

import androidx.paging.PagingData
import com.example.reservant_mobile.R
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import reservant_mobile.data.endpoints.Auth
import reservant_mobile.data.endpoints.User
import reservant_mobile.data.endpoints.Users
import reservant_mobile.data.endpoints.Wallet
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.FoundUserDTO
import reservant_mobile.data.models.dtos.LoggedUserDTO
import reservant_mobile.data.models.dtos.LoginCredentialsDTO
import reservant_mobile.data.models.dtos.MoneyDTO
import reservant_mobile.data.models.dtos.PageDTO
import reservant_mobile.data.models.dtos.RegisterUserDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.utils.GetUsersFilter


interface IUserService{
    suspend fun isLoginUnique(login: String): Boolean
    suspend fun registerUser(user: RegisterUserDTO): Result<Boolean>
    suspend fun loginUser(credentials: LoginCredentialsDTO): Result<Boolean>
    suspend fun logoutUser()
    suspend fun refreshToken(): Boolean

    /***
     * Return users by name. Returned UserDTO also contains friendStatus attribute.
     * Available filter values : see GetUsersFilter class
     */
    suspend fun getUsers(name: String, filter: GetUsersFilter? = null): Result<Flow<PagingData<FoundUserDTO>>?>
    suspend fun getUserInfo(): Result<UserDTO?>
    suspend fun editUserInfo(user: UserDTO): Result<UserDTO?>
    suspend fun getUserVisits(): Result<Flow<PagingData<VisitDTO>>?>
    suspend fun getUserVisitHistory(): Result<Flow<PagingData<VisitDTO>>?>
    suspend fun getUserCreatedEvents(): Result<List<EventDTO>?>
    suspend fun getUserInterestedEvents(): Result<Flow<PagingData<EventDTO>>?>
    //     TODO: add threads implementation
//     suspend fun getUserThreads(): Result<Flow<PagingData<ThreadDTO>>?>
    suspend fun addMoneyToWallet(money: MoneyDTO): Result<Boolean>
    suspend fun getWalletBalance(): Result<Double?>
    suspend fun getWalletHistory(): Result<Flow<PagingData<MoneyDTO>>?>
    suspend fun getUser(): Result<LoggedUserDTO?>

}

@OptIn(InternalSerializationApi::class)
class UserService(): ServiceUtil(), IUserService {
    private val localBearer = LocalBearerService()
    object UserObject {
        lateinit var userId: String
        lateinit var login: String
        lateinit var firstName: String
        lateinit var lastName: String
        lateinit var roles:List<String>

        internal fun clearData(){
            userId = ""
            login = ""
            firstName = ""
            lastName = ""
        }
    }

    private suspend fun wrapUser(u: UserDTO){
        u.userId?.let { UserObject.userId = it }
        UserObject.login = u.login!!
        UserObject.firstName = u.firstName
        UserObject.lastName = u.lastName
        UserObject.roles = u.roles!!
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
        val res = api.post(Auth.RegisterCustomer(), user)
        return booleanResultWrapper(res)
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
        UserObject.clearData()
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

    override suspend fun getUsers(name: String, filter: GetUsersFilter?): Result<Flow<PagingData<FoundUserDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Users(
                name = name,
                filter = filter?.toString(),
                page = page,
                perPage = perPage
            )
        )}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(FoundUserDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun getUserInfo(): Result<UserDTO?> {
        val res = api.get(User())
        return complexResultWrapper(res)
    }

    override suspend fun editUserInfo(user: UserDTO): Result<UserDTO?> {
        val res = api.put(User(), user)
        return complexResultWrapper(res)
    }

    override suspend fun getUserVisits(): Result<Flow<PagingData<VisitDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            User.Visits(
                page = page,
                perPage = perPage
            )
        )}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(VisitDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun getUserVisitHistory(): Result<Flow<PagingData<VisitDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            User.VisitHistory(
                page = page,
                perPage = perPage
            )
        )}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(VisitDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun getUserCreatedEvents(): Result<List<EventDTO>?> {
        val res = api.get(User.EventsCreated())
        return complexResultWrapper(res)
    }

    override suspend fun getUserInterestedEvents(): Result<Flow<PagingData<EventDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            User.EventsInterestedIn(
                page = page,
                perPage = perPage
            )
        )}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(EventDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun addMoneyToWallet(money: MoneyDTO): Result<Boolean> {
        val res = api.post(Wallet.AddMoney(), money)
        return booleanResultWrapper(res, HttpStatusCode.NoContent)
    }

    override suspend fun getWalletBalance(): Result<Double?> {
        val res = api.get(Wallet.Status())
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK)
            return try {
                val map:Map<String, Double?> = res.value.body()
                val value = map["balance"]!!
                Result(isError = false, value = value)
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }

        return Result(true, errorCodesWrapper(res.value), null)
    }

    override suspend fun getWalletHistory(): Result<Flow<PagingData<MoneyDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Wallet.History(
                page = page,
                perPage = perPage
            )
        )}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(MoneyDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun getUser(): Result<LoggedUserDTO?> {
        val res = api.get(User())

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