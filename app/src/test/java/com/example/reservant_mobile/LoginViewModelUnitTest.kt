package com.example.reservant_mobile

import com.example.reservant_mobile.ui.viewmodels.LoginViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test


class LoginViewModelUnitTest {

    @Test
    fun `empty login returns error string`() = runTest {
        val result = LoginViewModel().apply {
            login = ""
            password = "123"
        }.login()
        assertThat(result).isEqualTo(R.string.error_login_wrong_credentials)
    }


    @Test
    fun `empty password returns error string`() = runTest {
        val result = LoginViewModel().apply {
            login = "test"
            password = ""
        }.login()
        assertThat(result).isEqualTo(R.string.error_login_wrong_credentials)
    }

    @Test
    fun `valid login and password returns true`() = runTest{
        val result = LoginViewModel().apply {
            login = "john@doe.pl"
            password = "Pa${'$'}${'$'}w0rd"
        }.login()
        assertThat(result).isEqualTo(-1)
    }

}