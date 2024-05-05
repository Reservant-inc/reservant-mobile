package com.example.reservant_mobile.viewmodels

import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.ui.viewmodels.LoginViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test


class LoginViewModelUnitTest {

    @Test
    fun empty_login_returns_false() = runTest {
        val vm = LoginViewModel()
        vm.login.value = ""
        vm.password.value = "P@ssw0rd"
        val result = vm.login()
        assertThat(result).isEqualTo(false)
    }


    @Test
    fun empty_password_returns_false() = runTest {
        val result = LoginViewModel().apply {
            login = FormField(LoginCredentialsDTO::login.name).apply { value = "test" }
            password = FormField(LoginCredentialsDTO::password.name).apply { value = "" }
        }.login()
        assertThat(result).isEqualTo(false)
    }

    @Test
    fun valid_login_and_password_returns_true() = runTest{
        val result = LoginViewModel().apply {
            login = FormField(LoginCredentialsDTO::login.name).apply { value = "JD" }
            password = FormField(LoginCredentialsDTO::password.name).apply { value = "Pa${"$"}${"$"}w0rd" }
        }.login()
        assertThat(result).isEqualTo(true)
    }

}