package reservant_mobile.data.services

import androidx.paging.PagingData
import com.example.reservant_mobile.R
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import reservant_mobile.data.endpoints.Notifications
import reservant_mobile.data.models.dtos.NotificationDTO
import reservant_mobile.data.models.dtos.PageDTO
import reservant_mobile.data.models.dtos.fields.Result

interface INotificationService{
    suspend fun getNotifications(unreadOnly: Boolean = false): Result<Flow<PagingData<NotificationDTO>>?>
    suspend fun getBubbleInfo(): Result<Int?>
    suspend fun markAsRead(notificationIds: List<Int>): Result<Boolean>
}

@OptIn(InternalSerializationApi::class)
class NotificationService: ServiceUtil(), INotificationService {
    override suspend fun getNotifications(unreadOnly: Boolean): Result<Flow<PagingData<NotificationDTO>>?> {
        val call: suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage ->
            api.get(Notifications(
                unreadOnly = unreadOnly,
                page = page,
                perPage = perPage
            ))
        }

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(NotificationDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun getBubbleInfo(): Result<Int?> {
        val res = api.get(Notifications.Bubbles())
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                val r:Map<String, Int> = res.value.body()
                Result(isError = false, value = r["unreadNotificationCount"])
            }
            catch (e: Exception){
                println("SERVICE ERROR: $e")
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, errorCodesWrapper(res.value), null)
    }

    override suspend fun markAsRead(notificationIds: List<Int>): Result<Boolean> {
        val id: HashMap<String, List<Int>> = hashMapOf("notificationIds" to notificationIds)
        val res = api.post(Notifications.MarkRead(), id)
        return booleanResultWrapper(res)
    }
}