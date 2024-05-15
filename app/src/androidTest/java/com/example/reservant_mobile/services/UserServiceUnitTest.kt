package com.example.reservant_mobile.services

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UserServiceUnitTest: ServiceTest(){

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
        localBearer.saveBearerToken("test")
        userService.logoutUser()
        val token = localBearer.getBearerToken()
        assertThat(token).isEmpty()
    }

    @Test
    fun logout_clears_cache() = runTest {
        userService.loginUser(loginUser)
        assertThat(userService.refreshToken()).isTrue()
        userService.logoutUser()
        assertThat(userService.refreshToken()).isFalse()
    }
}