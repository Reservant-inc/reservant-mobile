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
    private val jdId = "e5779baf-5c9b-4638-b9e7-ec285e57b367"


    @Before
    fun setupData() = runBlocking {
        loginUser()
    }
    
    @Test
    fun accept_and_delete_friend_request()= runTest{
        assertThat(ser.sendFriendRequest(jdId).value).isTrue()
        assertThat(ser.markRequestAsRead(jdId).value).isTrue()
        assertThat(ser.acceptFriendRequest(jdId).value).isTrue()
        assertThat(ser.deleteFriendOrRequest(jdId).value).isTrue()
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