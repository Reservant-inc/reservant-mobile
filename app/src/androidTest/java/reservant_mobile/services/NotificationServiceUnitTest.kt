package reservant_mobile.services

import androidx.paging.testing.asSnapshot
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import reservant_mobile.data.services.INotificationService
import reservant_mobile.data.services.NotificationService

class NotificationServiceUnitTest: ServiceTest() {
    private val ser: INotificationService = NotificationService()

    @Before
    fun setupData() = runBlocking {
        loginUser()
    }

    @Test
    fun get_notifications_return_pagination()= runTest{
        val items = ser.getNotifications().value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun get_bubble_info_return_not_null()= runTest{
        assertThat(ser.getBubbleInfo().value).isNotNull()
    }

    @Test
    fun mark_notification_as_read_return_true()= runTest{
        assertThat(ser.markAsRead(listOf(1)).value).isTrue()
    }
}