package reservant_mobile.services

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import reservant_mobile.data.models.dtos.ThreadDTO
import reservant_mobile.data.services.IThreadsService
import reservant_mobile.data.services.ThreadsService

class ThreadServiceUnitTest:ServiceTest() {
    private val ser: IThreadsService = ThreadsService()
    private val jdId = "e5779baf-5c9b-4638-b9e7-ec285e57b367"
    private val customerId = "e08ff043-f8d2-45d2-b89c-aec4eb6a1f29"


    @Before
    fun setupData() = runBlocking {
        loginUser()
    }

    @Test
    fun create_and_delete_thread()= runTest{
        val res = ser.createThread(
            title = "Test thread",
            participantIds = listOf(jdId, customerId)
        ).value
        assertThat(res).isNotNull()
        assertThat(ser.deleteThread(res!!.threadId!!).value).isTrue()
    }

    @Test
    fun edit_thread_return_not_null()= runTest{
        val thread = ThreadDTO(
            title = "Test Thread Title"
        )
        assertThat(ser.editThread(1, thread).value).isNotNull()
    }

    @Test
    fun get_thread_return_not_null()= runTest{
        assertThat(ser.getThread(1).value).isNotNull()
    }

    @Test
    fun create_and_delete_message()= runTest{
        val res = ser.createMessage(1, "test message").value
        assertThat(res).isNotNull()
        assertThat(ser.deleteThread(res!!.messageId!!).value).isNotNull()
    }

    @Test
    fun get_message_return_pagination()= runTest{
        assertThat(ser.getMessages(1).value).isNotNull()
    }

    @Test
    fun edit_message_return_not_null()= runTest{
        assertThat(ser.editMessage(1, "message").value).isNotNull()
    }

    @Test
    fun mark_message_as_read_return_not_null()= runTest{
        assertThat(ser.markMessageAsRead(1).value).isNotNull()
    }
}