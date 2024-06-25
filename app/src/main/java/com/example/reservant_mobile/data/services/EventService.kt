package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.endpoints.Events
import com.example.reservant_mobile.data.models.dtos.EventDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

interface IEventService{
    suspend fun addEvent(event: EventDTO): Result<EventDTO?>
    suspend fun getEvent(eventId: Any): Result<EventDTO?>
    suspend fun markEventAsInterested(eventId: Any): Result<Boolean>
    suspend fun markEventAsNotInterested(eventId: Any): Result<Boolean>
}
class EventService(private var api: APIService = APIService()): IEventService {
    override suspend fun addEvent(event: EventDTO): Result<EventDTO?> {
        val res = api.post(Events(), event)

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

    override suspend fun getEvent(eventId: Any): Result<EventDTO?> {
        val res = api.get(Events.Id(eventId=eventId.toString()))

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
        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)    }

    override suspend fun markEventAsInterested(eventId: Any): Result<Boolean> {
        val res = api.post(Events.Id.Interested(parent = Events.Id(eventId=eventId.toString())), "")
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        if (res.value!!.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)    }

    override suspend fun markEventAsNotInterested(eventId: Any): Result<Boolean> {
        val res = api.delete(Events.Id.Interested(parent = Events.Id(eventId=eventId.toString())))
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        if (res.value!!.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)    }

}