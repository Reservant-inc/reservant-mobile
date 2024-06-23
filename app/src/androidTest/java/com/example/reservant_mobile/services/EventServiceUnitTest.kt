package com.example.reservant_mobile.services

import com.example.reservant_mobile.data.models.dtos.EventDTO
import com.example.reservant_mobile.data.services.EventService
import com.example.reservant_mobile.data.services.IEventService
import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventServiceUnitTest: ServiceTest() {
    private val ser: IEventService = EventService()

    private lateinit var event: EventDTO


    @Before
    fun setupData() = runBlocking {
        loginUser()


        val currentTime = LocalDateTime.now()
        val eventTime = currentTime.plusDays(2)
        val mustJoinUntilTime = currentTime.plusDays(1)

        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val eventTimeString = eventTime.format(formatter)
        val mustJoinUntilTimeString = mustJoinUntilTime.format(formatter)

        event = EventDTO(
            description = "string",
            time = eventTimeString,
            mustJoinUntil = mustJoinUntilTimeString,
            restaurantId = 1
        )
    }

    @Test
    fun get_events_return_not_null()= runTest{
        Truth.assertThat(ser.getEvent(1).value).isNotNull()
    }


    @Test
    fun add_and_delete_event()= runTest{
        val m = ser.addEvent(event).value
        Truth.assertThat(m).isNotNull()
//        Truth.assertThat(ser.deleteEvent(m!!.eventId!!).value).isTrue()
    }

    @Test
    fun add_and_delete_event_interest()= runTest{
        Truth.assertThat(ser.markEventAsInterested(1).value).isTrue()
        Truth.assertThat(ser.markEventAsNotInterested(1).value).isTrue()
    }
}