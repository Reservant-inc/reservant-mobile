package reservant_mobile.data.services

import androidx.paging.PagingData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import reservant_mobile.data.endpoints.Events
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.PageDTO
import reservant_mobile.data.models.dtos.fields.Result
import java.time.LocalDateTime

interface IEventService{
    suspend fun getEvents(origLat: Double? = null,
                          origLon: Double? = null,
                          restaurantId: Int? = null,
                          restaurantName: String? = null,
                          name: String? = null,
                          dateFrom: LocalDateTime? = null,
                          dateUntil: LocalDateTime? = null,
                          eventStatus: EventDTO.EventStatus? = null): Result<Flow<PagingData<EventDTO>>?>
    suspend fun addEvent(event: EventDTO): Result<EventDTO?>
    suspend fun getEvent(eventId: Any): Result<EventDTO?>
    suspend fun updateEvent(eventId: Any, event:EventDTO): Result<EventDTO?>
    suspend fun deleteEvent(eventId: Any): Result<Boolean>
    suspend fun markEventAsInterested(eventId: Any): Result<Boolean>
    suspend fun markEventAsNotInterested(eventId: Any): Result<Boolean>
    suspend fun acceptUser(eventId: Any, userId: String): Result<Boolean>
    suspend fun rejectUser(eventId: Any, userId: String): Result<Boolean>
    suspend fun getInterestedUser(eventId: Any): Result<Flow<PagingData<EventDTO.Participant>>?>

}
class EventService(): ServiceUtil(), IEventService{
    @OptIn(InternalSerializationApi::class)
    override suspend fun getEvents(
        origLat: Double?,
        origLon: Double?,
        restaurantId: Int?,
        restaurantName: String?,
        name: String?,
        dateFrom: LocalDateTime?,
        dateUntil: LocalDateTime?,
        eventStatus: EventDTO.EventStatus?
    ): Result<Flow<PagingData<EventDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Events(
                origLat = origLat,
                origLon = origLon,
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                name = name,
                dateFrom = dateFrom?.toString(),
                dateUntil = dateUntil?.toString(),
                eventStatus = eventStatus?.toString(),
                page = page,
                perPage = perPage
            )
        )}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(EventDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun addEvent(event: EventDTO): Result<EventDTO?> {
        val res = api.post(Events(), event)
        return complexResultWrapper(res)
    }

    override suspend fun getEvent(eventId: Any): Result<EventDTO?> {
        val res = api.get(Events.Id(eventId=eventId.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun updateEvent(eventId: Any, event: EventDTO): Result<EventDTO?> {
        val res = api.put(Events.Id(eventId=eventId.toString()), event)
        return complexResultWrapper(res)
    }

    override suspend fun deleteEvent(eventId: Any): Result<Boolean> {
        val res = api.delete(Events.Id(eventId=eventId.toString()))
        return booleanResultWrapper(res, expectedCode = HttpStatusCode.NoContent)
    }

    override suspend fun markEventAsInterested(eventId: Any): Result<Boolean> {
        val res = api.post(Events.Id.Interested(parent = Events.Id(eventId=eventId.toString())), "")
        return booleanResultWrapper(res, expectedCode = HttpStatusCode.NoContent)
    }

    override suspend fun markEventAsNotInterested(eventId: Any): Result<Boolean> {
        val res = api.delete(Events.Id.Interested(parent = Events.Id(eventId=eventId.toString())))
        return booleanResultWrapper(res, expectedCode = HttpStatusCode.NoContent)
    }

    override suspend fun acceptUser(eventId: Any, userId: String): Result<Boolean> {
        val res = api.post(Events.Id.AcceptUser(
            parent = Events.Id(eventId=eventId.toString()),
            userId = userId
        ),"")
        return booleanResultWrapper(res)
    }

    override suspend fun rejectUser(eventId: Any, userId: String): Result<Boolean> {
        val res = api.post(Events.Id.RejectUser(
            parent = Events.Id(eventId=eventId.toString()),
            userId = userId
        ),"")
        return booleanResultWrapper(res)
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun getInterestedUser(eventId: Any): Result<Flow<PagingData<EventDTO.Participant>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Events.Id.Interested(
                parent = Events.Id(eventId = eventId.toString()),
                page = page,
                perPage = perPage
            )
        )}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(EventDTO.Participant::class.serializer()))
        return pagingResultWrapper(sps)
    }

}