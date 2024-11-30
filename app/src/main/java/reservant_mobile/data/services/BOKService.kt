package reservant_mobile.data.services

import androidx.paging.PagingData
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import reservant_mobile.data.endpoints.Reports
import reservant_mobile.data.endpoints.Users
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.models.dtos.ReportDTO
import reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import reservant_mobile.data.models.dtos.UserSummaryDTO
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.utils.GetReservationStatus
import reservant_mobile.data.utils.GetRestaurantOrdersSort
import reservant_mobile.data.utils.GetVisitsSort
import java.time.LocalDateTime


class BOKService(): ServiceUtil() {
    private val restaurantService:IRestaurantService = RestaurantService()
    private val userService:IUserService = UserService()

    suspend fun getReports(
        dateFrom: LocalDateTime? = null,
        dateUntil: LocalDateTime?= null,
        category: ReportDTO.ReportCategory?= null,
        reportedUserId: String?= null,
        restaurantId: Int?= null
    ): Result<List<ReportDTO>?> {
        val res  = api.get(Reports(
            dateFrom = dateFrom?.toString(),
            dateUntil = dateUntil?.toString(),
            category = category?.name,
            reportedUserId = reportedUserId,
            restaurantId = restaurantId
            )
        )
        return complexResultWrapper(res)
    }

    suspend fun banUser(
        userId: String,
        timeSpan: String,
    ): Result<Boolean> {
        val dto = mapOf(
            "timeSpan" to timeSpan
        )
        val res = api.post(Users.UserId.Ban(
            parent = Users.UserId(userId = userId)), dto
        )
        return booleanResultWrapper(res, expectedCode = HttpStatusCode.NoContent)
    }

    suspend fun unbanUser(userId: String, ): Result<Boolean> {
        val res = api.post(Users.UserId.Unban(
            parent = Users.UserId(userId = userId)),""
        )
        return booleanResultWrapper(res, expectedCode = HttpStatusCode.NoContent)
    }

//    suspend fun resolveReport(reportId: String): Result<List<ReportDTO>?> {
//        val res = api.post(Users.UserId.Unban(
//            parent = Users.UserId(userId = userId)),""
//        )
//        return complexResultWrapper(res)
//    }

    suspend fun getRestaurantOrders(
        restaurantId: Any,
        returnFinished: Boolean?= null,
        orderBy: GetRestaurantOrdersSort?= null
    ): Result<Flow<PagingData<OrderDTO>>?> {
        return restaurantService.getRestaurantOrders(restaurantId,returnFinished,orderBy)
    }

    suspend fun getVisits(
        restaurantId: Any,
        dateStart: LocalDateTime?= null,
        dateEnd: LocalDateTime?= null,
        tableId: Int?= null,
        hasOrders: Boolean?= null,
        isTakeaway: Boolean?= null,
        reservationStatus: GetReservationStatus?= null,
        orderBy: GetVisitsSort?= null
    ): Result<Flow<PagingData<VisitDTO>>?> {
        return restaurantService.getVisits(restaurantId, dateStart, dateEnd, tableId, hasOrders, isTakeaway, reservationStatus, orderBy)
    }

    suspend fun getEmployees(
        restaurantId: Any,
        hallOnly: Boolean?= null,
        backdoorOnly: Boolean?= null
    ): Result<List<RestaurantEmployeeDTO>?> {
        return restaurantService.getEmployees(restaurantId, hallOnly, backdoorOnly)
    }

    suspend fun getUserSimpleInfo(userId: Any): Result<UserSummaryDTO?> {
        return userService.getUserSimpleInfo(userId)
    }
}