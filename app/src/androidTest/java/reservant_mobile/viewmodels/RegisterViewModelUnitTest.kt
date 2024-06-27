package reservant_mobile.viewmodels

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import reservant_mobile.ui.viewmodels.RegisterViewModel

class RegisterViewModelUnitTest {

    @Test
    fun form_returns_false_when_username_already_taken() = runTest {
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
        val result = vm.isLoginInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun error_when_login_is_empty()  {
        val vm = RegisterViewModel()
        vm.login.value = ""
        assertThat(vm.isLoginInvalid()).isTrue()
        vm.login.value = " "
        assertThat(vm.isLoginInvalid()).isTrue()
    }

    @Test
    fun error_when_login_is_invalid()  {
        val vm = RegisterViewModel()
        vm.login.value = "!@#%^&*()_-+="
        val result = vm.isLoginInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun no_error_when_first_name_is_correct()  {
        val vm = RegisterViewModel()
        vm.firstName.value = "John"
        val result = vm.isFirstNameInvalid()
        assertThat(result).isFalse()
    }
    @Test
    fun error_when_first_name_is_empty()  {
        val vm = RegisterViewModel()
        vm.firstName.value = ""
        assertThat(vm.isFirstNameInvalid()).isTrue()
        vm.firstName.value = " "
        assertThat(vm.isFirstNameInvalid()).isTrue()
    }

    @Test
    fun error_when_first_name_is_invalid()  {
        val vm = RegisterViewModel()
        vm.firstName.value = "!@#%^&*()_-+="
        val result = vm.isFirstNameInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun no_error_when_last_name_is_correct()  {
        val vm = RegisterViewModel()
        vm.lastName.value = "Doe"
        val result = vm.isLastNameInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun error_when_last_name_is_empty()  {
        val vm = RegisterViewModel()
        vm.lastName.value = ""
        assertThat(vm.isLastNameInvalid()).isTrue()
        vm.lastName.value = " "
        assertThat(vm.isLastNameInvalid()).isTrue()

    }

    @Test
    fun error_when_last_name_is_invalid()  {
        val vm = RegisterViewModel()
        vm.lastName.value = "!@#%^&*()_-+="
        val result = vm.isLastNameInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun no_error_when_birthdate_is_correct()  {
        val vm = RegisterViewModel()
        vm.birthday.value = "2020-02-20"
        val result = vm.isBirthDateInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun error_when_birthdate_is_empty()  {
        val vm = RegisterViewModel()
        vm.birthday.value = ""
        assertThat(vm.isBirthDateInvalid()).isTrue()
        vm.birthday.value = " "
        assertThat(vm.isBirthDateInvalid()).isTrue()
    }

    @Test
    fun error_when_birthdate_has_wrong_format() {
        val vm = RegisterViewModel()
        vm.birthday.value = "20-02-2020"
        val result = vm.isBirthDateInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun form_returns_no_error_when_email_is_correct(){
        val vm = RegisterViewModel()
        vm.email.value = "john@doe.pl"
        val result = vm.isEmailInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun form_returns_error_when_email_is_empty(){
        val vm = RegisterViewModel()
        vm.email.value = ""
        assertThat(vm.isEmailInvalid()).isTrue()
        vm.email.value = " "
        assertThat(vm.isEmailInvalid()).isTrue()
    }

    @Test
    fun error_when_email_has_wrong_format() {
        val vm = RegisterViewModel()
        vm.email.value = "invalid"
        val result = vm.isEmailInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun no_error_when_phone_number_is_correct(){
        val vm = RegisterViewModel()
        vm.phoneNum.value = "123456789"
        val result = vm.isPhoneInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun error_when_phone_number_is_empty(){
        val vm = RegisterViewModel()
        vm.phoneNum.value = ""
        assertThat(vm.isPhoneInvalid()).isTrue()
        vm.phoneNum.value = " "
        assertThat(vm.isPhoneInvalid()).isTrue()

    }

    @Test
    fun error_when_phone_number_has_wrong_format() {
        val vm = RegisterViewModel()
        vm.phoneNum.value = "1234"
        val result = vm.isPhoneInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun no_error_when_password_is_correct() {
        val vm = RegisterViewModel()
        vm.password.value = "P@ssw0rd"
        val result = vm.isPasswordInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun error_when_password_is_empty() {
        val vm = RegisterViewModel()
        vm.password.value = ""
        assertThat(vm.isPasswordInvalid()).isTrue()
        vm.password.value = " "
        assertThat(vm.isPasswordInvalid()).isTrue()
    }

    @Test
    fun error_when_password_is_invalid() {
        val vm = RegisterViewModel()
        vm.password.value = "invalid"
        val result = vm.isPasswordInvalid()
        assertThat(result).isTrue()
    }

    @Test
    fun error_when_confirm_password_is_empty() {
        val vm = RegisterViewModel()
        vm.password.value = "Passw0rd!"
        vm.confirmPassword.value = ""
        assertThat(vm.isConfirmPasswordDiff()).isTrue()
        vm.confirmPassword.value = " "
        assertThat(vm.isConfirmPasswordDiff()).isTrue()

    }

    @Test
    fun no_error_when_passwords_match() {
        val vm = RegisterViewModel()
        vm.password.value = "Passw0rd!"
        vm.confirmPassword.value = "Passw0rd!"
        val result = vm.isConfirmPasswordDiff()
        assertThat(result).isFalse()
    }
    @Test
    fun error_when_passwords_do_not_match() {
        val vm = RegisterViewModel()
        vm.password.value = "Passw0rd!"
        vm.confirmPassword.value = "P@ssw0rd"
        val result = vm.isConfirmPasswordDiff()
        assertThat(result).isTrue()
    }
}
