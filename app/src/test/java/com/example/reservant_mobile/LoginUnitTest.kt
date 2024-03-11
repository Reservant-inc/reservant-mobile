package com.example.reservant_mobile

import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO
import com.example.reservant_mobile.ui.viewmodels.LoginViewModel
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class LoginUnitTest {

    @Test
    fun `empty login returns false`() {

        val user = LoginCredentialsDTO("","123")
        val result = LoginViewModel.validate()
        assertThat(result).isFalse()
    }

    @Test
    fun `empty password returns false`() {

        val user = LoginCredentialsDTO("test","")
        val result = LoginViewModel.validate()
        assertThat(result).isFalse()
    }

    @Test
    fun `valid login and password returns true`() {

        val user = LoginCredentialsDTO("test","123")
        val result = LoginViewModel.validate()
        assertThat(result).isTrue()
    }

}