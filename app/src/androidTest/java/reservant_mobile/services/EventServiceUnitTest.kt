package reservant_mobile.services

import androidx.paging.testing.asSnapshot
import com.google.common.truth.Truth.assertThat
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
            name = "Biba w JD",
            description = "string",
            time = eventTimeString,
            maxPeople = 10,
            mustJoinUntil = mustJoinUntilTimeString,
            restaurantId = 1
         )
    }

    @Test
    fun get_events_return_pagination()= runTest{
        val items = ser.getEvents().value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun get_events_return_not_null()= runTest{
        assertThat(ser.getEvent(1).value).isNotNull()
    }


    @Test
    fun add_and_delete_event()= runTest{
        val e = ser.addEvent(event).value
        assertThat(e).isNotNull()
        assertThat(ser.deleteEvent(e!!.eventId!!).value).isTrue()
    }

    @Test
    fun update_event_return_not_null()= runTest{
        val e = ser.updateEvent(1,event).value
        assertThat(e).isNotNull()
    }

    @Test
    fun add_and_delete_event_interest()= runTest{
        assertThat(ser.markEventAsInterested(1).value).isTrue()
        assertThat(ser.markEventAsNotInterested(1).value).isTrue()
    }

    @Test
    fun accept_user_return_true()= runTest{
        assertThat(ser.acceptUser(1, "test").value).isTrue()
    }

    @Test
    fun reject_user_return_true()= runTest{
        assertThat(ser.rejectUser(1, "test").value).isTrue()
    }

    @Test
    fun get_interested_users_return_pagination()= runTest{
        val items = ser.getInterestedUser(1).value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }
}