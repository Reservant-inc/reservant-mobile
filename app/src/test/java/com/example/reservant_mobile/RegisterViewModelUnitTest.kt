package com.example.reservant_mobile

import com.example.reservant_mobile.ui.viewmodels.RegisterViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest

import org.junit.Test

class RegisterViewModelUnitTest {

    @Test
    fun `Form returns error code when username already taken`() = runTest {
        val result = RegisterViewModel().apply {
            login = "john@doe.pl"
            firstName = "John"
            lastName = "Dope"
            birthday = "2020-02-20"
            email = "john@doe.pl"
            phoneNum = "123456789"
            password = "Password123@"
            confirmPassword = "Password123@"
        }.register()

        assertThat(result).isEqualTo(R.string.error_register_username_taken)
    }

    @Test
    fun `Form returns no error when all fields are valid`() {
        val result = RegisterViewModel().apply {
            login = "JohnDope"
            firstName = "John"
            lastName = "Dope"
            birthday = "2020-02-20"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123@"
            confirmPassword = "Password123@"
        }.isRegisterInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun `Error when login is empty`()  {
        val result = RegisterViewModel().apply {
            login = ""
            firstName = "John"
            lastName = "Dope"
            birthday = "2020-02-20"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isLoginInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Error when login is invalid`()  {
        val result = RegisterViewModel().apply {
            login = "!@#%^&*()_-+="
            firstName = "John"
            lastName = "Dope"
            birthday = "2020-02-20"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isLoginInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Error when first name is empty`()  {
        val result = RegisterViewModel().apply {
            firstName = ""
            lastName = "Dope"
            birthday = "2020-02-20"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isFirstNameInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Error when first name is invalid`()  {
        val result = RegisterViewModel().apply {
            firstName = "!@#%^&*()_-+="
            lastName = "Dope"
            birthday = "2020-02-20"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isFirstNameInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Error when last name is empty`()  {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = ""
            birthday = "2020-02-20"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isLastNameInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Error when last name is invalid`()  {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "!@#%^&*()_-+="
            birthday = "2020-02-20"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isLastNameInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Error when birthdate is empty`()  {
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
    fun `Error when birthdate has wrong format`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "20-02-2020"
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
            birthday = "2020-02-20"
            email = ""
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isEmailInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Error when email has wrong format`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2020-02-20"
            email = "invalid"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isEmailInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Error when phone number is empty`(){
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2020-02-20"
            email = "john@test.com"
            phoneNum = ""
            password = "Password123"
            confirmPassword = "Password123"
        }.isPhoneInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Error when phone number has wrong format`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2020-02-20"
            email = "john@test.com"
            phoneNum = "1234"
            password = "Password123"
            confirmPassword = "Password123"
        }.isPhoneInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Error when password is empty`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2020-02-20"
            email = "john@test.com"
            phoneNum = "123456789"
            password = ""
            confirmPassword = "Password123"
        }.isPasswordInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Error when password is invalid`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2020-02-20"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "invalid"
            confirmPassword = "Password123"
        }.isPasswordInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `Error when confirm password is empty`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2020-02-20"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = ""
        }.isConfirmPasswordDiff()
        assertThat(result).isTrue()
    }

    @Test
    fun `Error when passwords do not match`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2020-02-20"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "invalid"
            confirmPassword = "Password123"
        }.isConfirmPasswordDiff()
        assertThat(result).isTrue()
    }
}