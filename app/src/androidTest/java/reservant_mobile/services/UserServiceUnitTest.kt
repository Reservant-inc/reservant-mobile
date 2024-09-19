package reservant_mobile.services

import androidx.paging.testing.asSnapshot
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import reservant_mobile.data.constants.PrefsKeys
import reservant_mobile.data.models.dtos.MoneyDTO
import kotlin.random.Random

class UserServiceUnitTest: ServiceTest(){

    @Before
    fun setupData() = runBlocking {
        loginUser()
    }

    @Test
    fun non_unique_login_returns_false() = runTest {
        assertThat(userService.isLoginUnique("JD")).isFalse()
    }

    @Test
    fun valid_login_returns_no_error() = runTest {
        assertThat(userService.loginUser(loginUser).isError).isFalse()
    }

    @Test
    fun refresh_token_returns_true() = runTest {
        userService.logoutUser()
        userService.loginUser(loginUser)
        assertThat(userService.refreshToken()).isTrue()
    }

    @Test
    fun logout_make_empty_token() = runTest {
        localBearer.saveData(PrefsKeys.BEARER_TOKEN, "test")
        userService.logoutUser()
        val token = localBearer.getData(PrefsKeys.BEARER_TOKEN)
        assertThat(token).isEmpty()
    }

    @Test
    fun logout_clears_cache() = runTest {
        userService.loginUser(loginUser)
        assertThat(userService.refreshToken()).isTrue()
        userService.logoutUser()
        assertThat(userService.refreshToken()).isFalse()
    }

    @Test
    fun get_users_return_pagination() = runTest {
        val items = userService.getUsers("John").value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun get_and_edit_userinfo() = runTest {
        val userInfo = userService.getUserInfo().value
        assertThat(userInfo).isNotNull()
        assertThat(userService.editUserInfo(userInfo!!).value).isNotNull()
    }

    @Test
    fun get_user_visits_return_pagination() = runTest {
        val items = userService.getUserVisits().value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun get_user_visit_history_return_pagination() = runTest {
        val items = userService.getUserVisitHistory().value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun get_created_events_return_not_null()= runTest{
        assertThat(userService.getUserCreatedEvents().value).isNotNull()
    }

    @Test
    fun get_interested_events_return_pagination()= runTest{
        val items = userService.getUserInterestedEvents().value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun add_money_return_true()= runTest{
        val money = MoneyDTO(
            title = "Tests",
            amount = Random(123).nextDouble()
        )
        assertThat(userService.addMoneyToWallet(money).value).isTrue()

    }

    @Test
    fun get_wallet_balance_return_not_null()= runTest{
        val res = userService.getWalletBalance().value
        assertThat(res).isNotNull()
        assertThat(res).isGreaterThan(0)
    }

    @Test
    fun get_wallet_history_return_pagination()= runTest{
        val items = userService.getWalletHistory().value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }
}