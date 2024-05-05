package com.example.reservant_mobile.activities

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.activities.RegisterActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterActivityTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var loginField: SemanticsMatcher
    private lateinit var nameField: SemanticsMatcher
    private lateinit var lastNameField: SemanticsMatcher
    private lateinit var dateField: SemanticsMatcher
    private lateinit var emailField: SemanticsMatcher
    private lateinit var passwordField: SemanticsMatcher
    private lateinit var repeatPasswordField: SemanticsMatcher
    private lateinit var phoneField: SemanticsMatcher
    private lateinit var signUpButton: SemanticsMatcher

    @Before
    fun setupLoginActivityNavHost() {
        rule.setContent {
            RegisterActivity(navController = rememberNavController())
        }

        this.loginField = hasSetTextAction() and hasText(rule.activity.getString(R.string.label_login))
        this.nameField = hasSetTextAction() and hasText(rule.activity.getString(R.string.label_name))
        this.lastNameField = hasSetTextAction() and hasText(rule.activity.getString(R.string.label_lastname))
        this.dateField = hasClickAction() and hasText(rule.activity.getString(R.string.label_register_birthday_select))
        this.emailField = hasSetTextAction() and hasText(rule.activity.getString(R.string.label_email))
        this.passwordField = hasSetTextAction() and hasText(rule.activity.getString(R.string.label_password))
        this.repeatPasswordField = hasSetTextAction() and hasText(rule.activity.getString(R.string.label_register_repeat_password))
        this.phoneField = hasSetTextAction() and hasText(rule.activity.getString(R.string.label_register_repeat_password))
        this.signUpButton = hasClickAction() and hasTestTag("Button") and hasText(rule.activity.getString(R.string.label_signup))
    }

    @Test
    fun enterInvalidLogin_showError() {
//        test empty input
        rule.onNode(loginField).performTextInput("")
        rule.onNode(nameField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_login_invalid)).assertExists()

//        test invalid input
        rule.onNode(loginField).performTextInput("!@#$%^&*()_-+=")
        rule.onNode(nameField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_login_invalid)).assertExists()
    }

// Waiting for proper implementation
/*    @Test
    fun enterTakenLogin_showError() {
        rule.onNode(loginField).performTextInput("john@doe.pl")
        rule.onNode(nameField).performClick()

        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
    }*/

    @Test
    fun enterInvalidName_showError() {
//        test empty input
        rule.onNode(nameField).performTextInput("")
        rule.onNode(loginField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_invalid_name)).assertExists()

//        test invalid input
        rule.onNode(nameField).performTextInput("!@#$%^&*()_-+=")
        rule.onNode(loginField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_invalid_name)).assertExists()
    }

    @Test
    fun enterInvalidLastName_showError() {
//        test empty input
        rule.onNode(lastNameField).performTextInput("")
        rule.onNode(loginField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_invalid_lastname)).assertExists()

//        test invalid input
        rule.onNode(lastNameField).performTextInput("!@#$%^&*()_-+=")
        rule.onNode(loginField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_invalid_lastname)).assertExists()
    }

    @Test
    fun ensureDateDialogShowUp() {
        rule.onNode(dateField).performClick()
        rule.onNode(isDialog()).assertExists()
    }

    @Test
    fun enterInvalidEmail_showError() {
//        test empty input
        rule.onNode(emailField).performTextInput("")
        rule.onNode(loginField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_invalid_email)).assertExists()

//        test invalid input
        rule.onNode(emailField).performTextInput("!@#$%^&*()_-+=")
        rule.onNode(loginField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_invalid_email)).assertExists()
    }

// Waiting for proper implementation
/*    @Test
    fun enterInvalidPhone_showError() {
        rule.onNode(phoneField).performTextInput("!@#$%^&*()_-+=")
        rule.onNode(loginField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_invalid_phone)).assertExists()

        rule.onNode(phoneField).performTextInput("123")
        rule.onNode(loginField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_invalid_phone)).assertExists()

        rule.onNode(phoneField).performTextInput("1231231231231231")
        rule.onNode(loginField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_invalid_phone)).assertExists()
    }*/
@Test
fun ensureCountryPickerDialogShowUp() {
    rule.onNode(hasTestTag("CountryPicker")).performClick()
    rule.onNode(isDialog()).assertExists()
}

    @Test
    fun enterInvalidPassword_showError() {
//        test empty input
        rule.onNode(passwordField).performTextInput("")
        rule.onNode(loginField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_invalid_password)).assertExists()

//        test invalid input
        rule.onNode(passwordField).performTextInput("invalid")
        rule.onNode(loginField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_invalid_password)).assertExists()
    }

    @Test
    fun enterInvalidRepeatedPassword_showError() {
        rule.onNode(passwordField).performTextInput("P@ssw0rd")

//        test empty input
        rule.onNode(repeatPasswordField).performTextInput("")
        rule.onNode(loginField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_password_match)).assertExists()

//        test invalid input
        rule.onNode(repeatPasswordField).performTextInput("invalid")
        rule.onNode(loginField).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_password_match)).assertExists()
    }

    @Test
    fun enterEmptyForm_showError() {
        rule.onNode(signUpButton).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.error_register_invalid_request)).assertExists()
    }

}