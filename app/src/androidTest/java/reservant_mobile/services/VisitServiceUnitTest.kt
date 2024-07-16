package reservant_mobile.services

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.services.IVisitsService
import reservant_mobile.data.services.VisitsService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class VisitServiceUnitTest: ServiceTest() {
    private val ser: IVisitsService = VisitsService()

    private lateinit var visit: VisitDTO


    @Before
    fun setupData() = runBlocking {
        loginUser()


        val currentTime = LocalDateTime.now()
        val visitTime = currentTime.plusDays(2)

        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val visitTimeString = visitTime.format(formatter)

        visit = VisitDTO(
            date = visitTimeString,
            numberOfGuests = 1,
            tip = 0.0,
            takeaway = false,
            restaurantId = 1,
            tableId = 1,
            participants = emptyList()
            )
    }

    @Test
    fun get_visit_return_not_null()= runTest{
        assertThat(ser.getVisit(1).value).isNotNull()
    }

    @Test
    fun get_order_return_not_null()= runTest{
        assertThat(ser.createVisit(visit).value).isNotNull()
    }
}