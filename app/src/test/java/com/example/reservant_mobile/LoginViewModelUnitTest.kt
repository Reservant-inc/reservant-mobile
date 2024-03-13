package com.example.reservant_mobile

import com.example.reservant_mobile.ui.viewmodels.LoginViewModel
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class LoginViewModelUnitTest {

    @Test
    fun `empty login returns false`() {
        val result = LoginViewModel().apply {
            login = ""
            password = "123"
        }.validateLogin()
        assertThat(result).isFalse()
    }


    @Test
    fun `empty password returns false`() {
        val result = LoginViewModel().apply {
            login = "test"
            password = ""
        }.validateLogin()
        assertThat(result).isFalse()
    }

    @Test
    fun `valid login and password returns true`() {
        val result = LoginViewModel().apply {
            login = "test"
            password = "123"
        }.validateLogin()
        assertThat(result).isTrue()
    }

}