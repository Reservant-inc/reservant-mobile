package com.example.reservant_mobile.services

import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO
import com.example.reservant_mobile.data.models.dtos.RegisterUserDTO
import com.example.reservant_mobile.data.models.dtos.UserDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.services.IUserService
import com.example.reservant_mobile.data.services.LocalBearerService
import com.example.reservant_mobile.data.services.UserService
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class UserServiceUnitTest {
    private val localBearer = LocalBearerService()
    private val ser:IUserService = UserService()

    private lateinit var loginUser: LoginCredentialsDTO
    private lateinit var existingToken: String


    @Before
    fun setupData() = runBlocking {
        existingToken =  localBearer.getBearerToken()
        loginUser= LoginCredentialsDTO(
            login = "JD",
            password = "Pa${"$"}${"$"}w0rd",
            rememberMe = false
        )
    }
    @Test
    fun non_unique_login_returns_false() = runTest {
        assertThat(ser.isLoginUnique("JD")).isFalse()
    }

    @Test
    fun valid_login_returns_no_error() = runTest {
        assertThat(ser.loginUser(loginUser).isError).isFalse()
    }

    @Test
    fun refresh_token_returns_true() = runTest {
        ser.logoutUser()
        ser.loginUser(loginUser)
        assertThat(ser.refreshToken()).isTrue()
    }

    @Test
    fun logout_make_empty_token() = runTest {
        localBearer.saveBearerToken("test")
        ser.logoutUser()
        val token = localBearer.getBearerToken()
        assertThat(token).isEmpty()
    }

    @Test
    fun logout_clears_cache() = runTest {
        ser.loginUser(loginUser)
        assertThat(ser.refreshToken()).isTrue()
        ser.logoutUser()
        assertThat(ser.refreshToken()).isFalse()
    }

    @After
    fun cleanPostTestData() = runBlocking{
        if(existingToken.isNotEmpty())
            localBearer.saveBearerToken(existingToken)
    }
}