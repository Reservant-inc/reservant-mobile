package reservant_mobile.services

import com.google.common.truth.Truth
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import reservant_mobile.data.services.FriendsService
import reservant_mobile.data.services.IFriendsService

class FriendsServiceUnitTest:ServiceTest() {
    private val ser: IFriendsService = FriendsService()
    private val jdId = "e5779baf-5c9b-4638-b9e7-ec285e57b367"
    private val customerId = "e08ff043-f8d2-45d2-b89c-aec4eb6a1f29"


    @Before
    fun setupData() = runBlocking {
        loginUser()
    }

    @Test
    fun send_friend_request_return_true()= runTest{
        Truth.assertThat(ser.sendFriendRequest(jdId).value).isTrue()
    }

    @Test
    fun mark_request_as_read_return_true()= runTest{
        Truth.assertThat(ser.markRequestAsRead(jdId).value).isTrue()
    }

    @Test
    fun accept_and_delete_friend_friend_request()= runTest{
        Truth.assertThat(ser.acceptFriendRequest(jdId).value).isTrue()
        Truth.assertThat(ser.deleteFriendOrRequest(jdId).value).isTrue()
    }


    @Test
    fun get_friends_return_not_null()= runTest{
        Truth.assertThat(ser.getFriends().value!!.count()).isGreaterThan(1)
    }

    @Test
    fun get_incoming_requests_return_not_null()= runTest{
        Truth.assertThat(ser.getIncomingFriendRequests().value).isNotNull()
    }

    @Test
    fun get_outgoing_requests_return_not_null()= runTest{
        Truth.assertThat(ser.getOutgoingFriendRequests().value).isNotNull()
    }

}