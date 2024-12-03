package reservant_mobile.data.services

import androidx.paging.PagingData
import com.example.reservant_mobile.R
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import reservant_mobile.data.constants.PrefsKeys
import reservant_mobile.data.endpoints.Auth
import reservant_mobile.data.endpoints.Reports
import reservant_mobile.data.endpoints.User
import reservant_mobile.data.endpoints.Users
import reservant_mobile.data.endpoints.Wallet
import reservant_mobile.data.models.dtos.EmploymentDTO
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.FoundUserDTO
import reservant_mobile.data.models.dtos.LoginCredentialsDTO
import reservant_mobile.data.models.dtos.MoneyDTO
import reservant_mobile.data.models.dtos.PageDTO
import reservant_mobile.data.models.dtos.RegisterUserDTO
import reservant_mobile.data.models.dtos.ReportDTO
import reservant_mobile.data.models.dtos.ThreadDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.models.dtos.UserSettingsDTO
import reservant_mobile.data.models.dtos.UserSummaryDTO
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.utils.GetUserEventsCategory
import reservant_mobile.data.utils.GetUserEventsSort
import reservant_mobile.data.utils.GetUsersFilter
import reservant_mobile.data.utils.GetVisitsSort
import java.time.LocalDateTime


interface IUserService{
    suspend fun isLoginUnique(login: String): Boolean
    suspend fun registerUser(user: RegisterUserDTO): Result<Boolean>
    suspend fun loginUser(credentials: LoginCredentialsDTO): Result<Boolean>
    suspend fun logoutUser()
    suspend fun refreshToken(): Boolean
    suspend fun registerFCMToken(token: String): Boolean
    suspend fun unregisterFCMToken(token: String): Boolean


    /***
     * Return users by name. Returned UserDTO also contains friendStatus attribute.
     * Available filter values : see GetUsersFilter class
     */
    suspend fun getUsers(name: String, filter: GetUsersFilter? = null): Result<Flow<PagingData<FoundUserDTO>>?>
    suspend fun getUserInfo(): Result<UserDTO?>
    suspend fun editUserInfo(user: UserDTO): Result<UserDTO?>
    suspend fun getUserVisits(): Result<Flow<PagingData<VisitDTO>>?>
    suspend fun getUserVisitHistory(): Result<Flow<PagingData<VisitDTO>>?>
    suspend fun getUserEvents(
        dateFrom: LocalDateTime? = null,
        dateUntil: LocalDateTime? = null,
        orderBy: GetUserEventsSort? = null,
        category: GetUserEventsCategory? = null
    ): Result<Flow<PagingData<EventDTO>>?>
    suspend fun getUserThreads(): Result<Flow<PagingData<ThreadDTO>>?>
    suspend fun getUserEmployments(returnTerminated: Boolean? = null): Result<List<EmploymentDTO>?>
    suspend fun addMoneyToWallet(money: MoneyDTO): Result<Boolean>
    suspend fun getWalletBalance(): Result<Double?>
    suspend fun getWalletHistory(): Result<Flow<PagingData<MoneyDTO>>?>
    suspend fun getUserSimpleInfo(userId: Any): Result<UserSummaryDTO?>
    suspend fun getUserSettings(): Result<UserSettingsDTO?>
    suspend fun updateUserSettings(settings: UserSettingsDTO): Result<UserSettingsDTO?>
    suspend fun updateUserProfile(user: UserDTO): Result<Boolean>
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Boolean>
    suspend fun getReports(dateFrom: LocalDateTime? = null,
                           dateUntil: LocalDateTime? = null,
                           category: ReportDTO.ReportCategory? = null,
                           reportedUserId: String? = null,
                           restaurantId: Int? = null): Result<List<ReportDTO>?>
}

@OptIn(InternalSerializationApi::class)
class UserService(): ServiceUtil(), IUserService {
    private val localDataService = LocalDataService()
    object UserObject {
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
        u.userId?.let { UserObject.userId = it }
        UserObject.login = u.login!!
        UserObject.firstName = u.firstName
        UserObject.lastName = u.lastName
        UserObject.roles = u.roles!!
        localDataService.saveData(PrefsKeys.BEARER_TOKEN, u.token!!)
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

    override suspend fun updateUserProfile(user: UserDTO): Result<Boolean> {
        val res = api.put(User(), user)
        return booleanResultWrapper(res)
    }

    override suspend fun loginUser(credentials: LoginCredentialsDTO): Result<Boolean> {
        val fcmToken = localDataService.getData(PrefsKeys.FCM_TOKEN)
        val loginData = credentials.copy(firebaseDeviceToken = fcmToken)

        //return errors in toast when connection error
        val res = api.post(Auth.Login(), loginData)
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
        val fcmToken = localDataService.getData(PrefsKeys.FCM_TOKEN)
        if(fcmToken.isNotEmpty()){
            unregisterFCMToken(fcmToken)
            localDataService.saveData(PrefsKeys.FCM_TOKEN, "")
        }
        UserObject.clearData()
        api.clearToken()
    }

    override suspend fun refreshToken(): Boolean {
        val fcmToken = localDataService.getData(PrefsKeys.FCM_TOKEN)
        if(fcmToken.isNotEmpty()){
            registerFCMToken(fcmToken)
        }

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

    override suspend fun registerFCMToken(token: String): Boolean {
        val body: HashMap<String, String> = hashMapOf("deviceToken" to token)
        val res = api.post(Auth.RegisterFirebaseToken(), body)
        if(res.isError)
            return false

        return res.value!!.status == HttpStatusCode.OK
    }

    override suspend fun unregisterFCMToken(token: String): Boolean {
        val body: HashMap<String, String> = hashMapOf("deviceToken" to token)
        val res = api.post(Auth.UnregisterFirebaseToken(), body)
        if(res.isError)
            return false

        return res.value!!.status == HttpStatusCode.OK    }

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

    override suspend fun getUserEvents(dateFrom: LocalDateTime?, dateUntil: LocalDateTime?, orderBy: GetUserEventsSort?, category: GetUserEventsCategory?): Result<Flow<PagingData<EventDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            User.Events(
                dateFrom = dateFrom?.toString(),
                dateUntil = dateUntil?.toString(),
                order = orderBy?.name,
                category = category?.name,
                page = page,
                perPage = perPage
            )
        )}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(EventDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun getUserThreads(): Result<Flow<PagingData<ThreadDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            User.Threads(
                page = page,
                perPage = perPage
            )
        )}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(ThreadDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun getUserEmployments(returnTerminated: Boolean?): Result<List<EmploymentDTO>?> {
        val res = api.get(User.Employments(returnTerminated = returnTerminated))
        return complexResultWrapper(res)
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

    override suspend fun getUserSimpleInfo(userId: Any): Result<UserSummaryDTO?> {
        val res = api.get(Users.UserId(userId = userId.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun getUserSettings(): Result<UserSettingsDTO?> {
        val res = api.get(User.Settings())
        return complexResultWrapper(res)
    }

    override suspend fun updateUserSettings(settings: UserSettingsDTO): Result<UserSettingsDTO?> {
        val res = api.put(User.Settings(), settings)
        return complexResultWrapper(res)
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Boolean> {
        val obj = mapOf(
            "oldPassword" to oldPassword,
            "newPassword" to newPassword
        )
        val res = api.post(Auth.ChangePassword(), obj)
        return booleanResultWrapper(res, expectedCode = HttpStatusCode.NoContent)
    }

    override suspend fun getReports(
        dateFrom: LocalDateTime?,
        dateUntil: LocalDateTime?,
        category: ReportDTO.ReportCategory?,
        reportedUserId: String?,
        restaurantId: Int?
    ): Result<List<ReportDTO>?> {
        val res  = api.get(User.Reports(
            dateFrom = dateFrom?.toString(),
            dateUntil = dateUntil?.toString(),
            category = category?.name,
            reportedUserId = reportedUserId,
            restaurantId = restaurantId
            )
        )
        return complexResultWrapper(res)
    }

}