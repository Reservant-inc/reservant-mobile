package reservant_mobile.viewmodels

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import reservant_mobile.ui.viewmodels.LoginViewModel


class LoginViewModelUnitTest {

    @Test
    fun empty_login_returns_false() = runTest {
        val vm = LoginViewModel()
        vm.login.value = ""
        vm.password.value = "P@ssw0rd"
        assertThat(vm.login()).isEqualTo(false)
        vm.login.value = " "
        assertThat(vm.login()).isEqualTo(false)

    }

    @Test
    fun empty_password_returns_false() = runTest {
        val vm = LoginViewModel()
        vm.login.value = "test"
        vm.password.value = ""
        assertThat(vm.login()).isEqualTo(false)
        vm.password.value = " "
        assertThat(vm.login()).isEqualTo(false)

    }

    @Test
    fun valid_login_and_password_returns_true() = runTest{
        val vm = LoginViewModel()
        vm.login.value = "JD"
        vm.password.value = "Pa${"$"}${"$"}w0rd"
        val result = vm.login()
        assertThat(result).isEqualTo(true)
    }

}