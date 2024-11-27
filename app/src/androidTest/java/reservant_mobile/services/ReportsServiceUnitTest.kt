package reservant_mobile.services

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import reservant_mobile.data.services.IReportsService
import reservant_mobile.data.services.ReportsService

class ReportsServiceUnitTest(): ServiceTest() {
    private val ser: IReportsService = ReportsService()
    private val visitId = 1
    private val customerId = "a79631a0-a3bf-43fa-8fbe-46e5ee697eeb"
    private val empId = "06c12721-e59e-402f-aafb-2b43a4dd23f2"
    private val description = "Test description"


    @Before
    fun setupData() = runBlocking {
        loginUser()
    }

    @Test
    fun report_customer_return_not_null()= runTest{
        val report = ser.reportCustomer(description = description, reportedUserId = customerId, visitId = visitId).value
        assertThat(report).isNotNull()
    }

    @Test
    fun report_employee_return_not_null()= runTest{
        val report = ser.reportEmployee(description = description, reportedUserId = empId, visitId = visitId).value
        assertThat(report).isNotNull()
    }

    @Test
    fun report_lost_item_return_not_null()= runTest{
        val report = ser.reportLostItem(description = description, visitId = visitId).value
        assertThat(report).isNotNull()
    }

    @Test
    fun report_bug_return_not_null()= runTest{
        val report = ser.reportBug(description = description).value
        assertThat(report).isNotNull()
    }

}