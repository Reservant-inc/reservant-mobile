package reservant_mobile.services

import androidx.paging.testing.asSnapshot
import com.google.common.truth.Truth.assertThat
import io.ktor.websocket.close
import kotlinx.coroutines.isActive
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
        val items = ser.getNotifications(unreadOnly = false).value
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

    @Test
    fun receive_notification_session_return_not_null()= runTest{
        val session = ser.getNotificationSession().value!!
        assertThat(session).isNotNull()
        session.close()
    }
}