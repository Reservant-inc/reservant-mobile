package reservant_mobile.services

import androidx.paging.testing.asSnapshot
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import reservant_mobile.data.services.FriendsService
import reservant_mobile.data.services.IFriendsService

class FriendsServiceUnitTest:ServiceTest() {
    private val ser: IFriendsService = FriendsService()
    private val customerId = "e08ff043-f8d2-45d2-b89c-aec4eb6a1f29"


    @Before
    fun setupData() = runBlocking {
        loginUser()
    }
    
    @Test
    fun accept_and_delete_friend_request()= runTest{
        assertThat(ser.sendFriendRequest(customerId).value).isTrue()
        assertThat(ser.markRequestAsRead(customerId).value).isTrue()
        assertThat(ser.acceptFriendRequest(customerId).value).isTrue()
        assertThat(ser.deleteFriendOrRequest(customerId).value).isTrue()
    }

    @Test
    fun get_friends_return_not_null()= runTest{
        val items = ser.getFriends().value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun get_incoming_requests_return_not_null()= runTest{
        val items = ser.getIncomingFriendRequests().value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun get_outgoing_requests_return_not_null()= runTest{
        val items = ser.getOutgoingFriendRequests().value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

}