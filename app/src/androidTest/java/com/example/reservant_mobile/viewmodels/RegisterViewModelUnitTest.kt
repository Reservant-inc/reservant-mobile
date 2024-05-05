package com.example.reservant_mobile.viewmodels

import com.example.reservant_mobile.ui.viewmodels.RegisterViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest

import org.junit.Test

class RegisterViewModelUnitTest {

    @Test
    fun form_returns_error_false_username_already_taken() = runTest {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.register()
        assertThat(result).isFalse()
    }

    @Test
    fun form_returns_no_error_when_all_fields_are_valid() {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isRegisterInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun no_error_when_login_is_correct()  {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isLoginInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun error_when_login_is_empty()  {
        val vm = RegisterViewModel()
        vm.login.value = ""
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isLoginInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun error_when_login_is_invalid()  {
        val vm = RegisterViewModel()
        vm.login.value = "!@#%^&*()_-+="
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isLoginInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun no_error_when_first_name_is_correct()  {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isFirstNameInvalid()
        assertThat(result).isFalse()
    }
    @Test
    fun error_when_first_name_is_empty()  {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = ""
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isFirstNameInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun error_when_first_name_is_invalid()  {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "!@#%^&*()_-+="
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isFirstNameInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun no_error_when_last_name_is_correct()  {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isLastNameInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun error_when_last_name_is_empty()  {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = ""
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isLastNameInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun error_when_last_name_is_invalid()  {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "!@#%^&*()_-+="
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isLastNameInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun no_error_when_birthdate_is_correct()  {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isBirthDateInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun error_when_birthdate_is_empty()  {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = ""
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isBirthDateInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun error_when_birthdate_has_wrong_format() {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "20-02-2020"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isBirthDateInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun form_returns_no_error_when_email_is_correct(){
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isEmailInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun form_returns_error_when_email_is_empty(){
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = ""
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isEmailInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun error_when_email_has_wrong_format() {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "invalid"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isEmailInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun no_error_when_phone_number_is_correct(){
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isPhoneInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun error_when_phone_number_is_empty(){
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = ""
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isPhoneInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun error_when_phone_number_has_wrong_format() {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "1234"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isPhoneInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun no_error_when_password_is_correct() {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isPasswordInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun error_when_password_is_empty() {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = ""
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isPasswordInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun error_when_password_is_invalid() {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "invalid"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isPasswordInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun error_when_confirm_password_is_empty() {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "P@ssw0rd"
        vm.confirmPassword.value = ""
        val result = vm.isConfirmPasswordDiff()
        assertThat(result).isTrue()
    }

    @Test
    fun no_error_when_passwords_match() {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "Passw0rd!"
        vm.confirmPassword.value = "Passw0rd!"
        val result = vm.isConfirmPasswordDiff()
        assertThat(result).isFalse()
    }
    @Test
    fun error_when_passwords_do_not_match() {
        val vm = RegisterViewModel()
        vm.login.value = "JD"
        vm.firstName.value = "John"
        vm.lastName.value = "Doe"
        vm.birthday.value = "2020-02-20"
        vm.email.value = "john@doe.pl"
        vm.phoneNum.value = "123456789"
        vm.password.value = "Passw0rd!"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isConfirmPasswordDiff()
        assertThat(result).isTrue()
    }
}
