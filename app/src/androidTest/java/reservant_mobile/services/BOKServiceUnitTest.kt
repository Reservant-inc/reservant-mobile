package reservant_mobile.services

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import reservant_mobile.data.models.dtos.LoginCredentialsDTO
import reservant_mobile.data.services.BOKService

class BOKServiceUnitTest: ServiceTest() {
    private val ser = BOKService()
    private val banUserId = "e5779baf-5c9b-4638-b9e7-ec285e57b367"


    @Before
    fun setupData() = runBlocking {
        val bokUser: LoginCredentialsDTO = LoginCredentialsDTO(
            login = "support@mail.com",
            password = "Pa${"$"}${"$"}w0rd",
            rememberMe = false
        )
        loginUser(bokUser)
    }

    @Test
    fun get_reports_return_not_null()= runTest{
        assertThat(ser.getReports().value).isNotNull()
    }

    @Test
    fun bun_unban_user_return_true()= runTest{
        assertThat(ser.banUser(banUserId, "20:15:15").value).isTrue()
        assertThat(ser.unbanUser(banUserId).value).isTrue()
    }
}