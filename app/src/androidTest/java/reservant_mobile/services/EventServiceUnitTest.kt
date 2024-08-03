package reservant_mobile.services

import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.services.EventService
import reservant_mobile.data.services.IEventService
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
        val e = ser.addEvent(event).value
        Truth.assertThat(e).isNotNull()
        Truth.assertThat(ser.deleteEvent(e!!.eventId!!).value).isTrue()
    }

    @Test
    fun update_event_return_not_null()= runTest{
        val e = ser.updateEvent(1,event).value
        Truth.assertThat(e).isNotNull()
    }

    @Test
    fun add_and_delete_event_interest()= runTest{
        Truth.assertThat(ser.markEventAsInterested(1).value).isTrue()
        Truth.assertThat(ser.markEventAsNotInterested(1).value).isTrue()
    }
}