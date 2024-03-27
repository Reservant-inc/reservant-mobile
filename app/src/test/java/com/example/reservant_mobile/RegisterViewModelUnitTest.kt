package com.example.reservant_mobile

import com.example.reservant_mobile.ui.viewmodels.RegisterViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest

import org.junit.Test

class RegisterViewModelUnitTest {

    @Test
    fun `Form returns error code when username already taken`() = runTest {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "21-02-2002"
            email = "john@doe.pl"
            phoneNum = "123456789"
            password = "Password123@"
            confirmPassword = "Password123@"
        }.register()
        assertThat(result).isNotEqualTo(-1)
    }

    @Test
    fun `Form returns no error when all fields are valid`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "01-02-2002"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123@"
            confirmPassword = "Password123@"
        }.isRegisterInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun `Form returns error when first name is empty`()  {
        val result = RegisterViewModel().apply {
            firstName = ""
            lastName = "Dope"
            birthday = "20-02-2002"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isFirstNameInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Form returns error when last name is empty`()  {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = ""
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isLastNameInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Form returns error when birthdate is empty`()  {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = ""
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isBirthDateInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Form returns error when birthdate has wrong format`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "invalid"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isBirthDateInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Form returns error when email is empty`(){
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = ""
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isEmailInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Form returns error when email has wrong format`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "invalid"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isEmailInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Form returns error when phone number is empty`(){
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = ""
            password = "Password123"
            confirmPassword = "Password123"
        }.isPhoneInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Form returns error when phone number has wrong format`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = "invalid"
            password = "Password123"
            confirmPassword = "Password123"
        }.isPhoneInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Form returns error when password is empty`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = "123456789"
            password = ""
            confirmPassword = "Password123"
        }.isPasswordInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Form returns error when confirm password is empty`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = ""
        }.isConfirmPasswordDiff()
        assertThat(result).isTrue()
    }

    @Test
    fun `Form returns error when passwords do not match`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "invalid"
            confirmPassword = "Password123"
        }.isConfirmPasswordDiff()
        assertThat(result).isTrue()
    }
}