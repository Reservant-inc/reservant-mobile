package com.example.reservant_mobile

import com.example.reservant_mobile.ui.viewmodels.RegisterViewModel
import com.google.common.truth.Truth.assertThat

import org.junit.Test

class RegisterViewModelUnitTest {

    @Test
    fun `validateForm returns true when all fields are valid`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isRegisterInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun `validateForm returns false when first name is empty`() {
        val result = RegisterViewModel().apply {
            firstName = ""
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isRegisterInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun `validateForm returns false when last name is empty`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = ""
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isRegisterInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun `validateForm returns false when birthdate is empty`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = ""
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isRegisterInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun `validateForm returns false when birthdate has wrong format`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "invalid"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isRegisterInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun `validateForm returns false when email is empty`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = ""
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isRegisterInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun `validateForm returns false when email has wrong format`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "invalid"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = "Password123"
        }.isRegisterInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun `validateForm returns false when phone number is empty`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = ""
            password = "Password123"
            confirmPassword = "Password123"
        }.isRegisterInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun `validateForm returns false when phone number has wrong format`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = "invalid"
            password = "Password123"
            confirmPassword = "Password123"
        }.isRegisterInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun `validateForm returns false when password is empty`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = "123456789"
            password = ""
            confirmPassword = "Password123"
        }.isRegisterInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun `validateForm returns false when confirm password is empty`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "Password123"
            confirmPassword = ""
        }.isRegisterInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun `validateForm returns false when passwords do not match`() {
        val result = RegisterViewModel().apply {
            firstName = "John"
            lastName = "Dope"
            birthday = "2001-02-22"
            email = "john@test.com"
            phoneNum = "123456789"
            password = "invalid"
            confirmPassword = "Password123"
        }.isRegisterInvalid()
        assertThat(result).isFalse()
    }
}