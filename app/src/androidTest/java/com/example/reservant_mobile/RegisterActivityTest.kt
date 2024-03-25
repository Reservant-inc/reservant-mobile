package com.example.reservant_mobile

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.reservant_mobile.ui.activities.RegisterActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterActivityTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupLoginActivityNavHost() {
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            RegisterActivity(navController = navController)
        }
    }

    @Test
    fun restaurant() {
//        val loginLabel = rule.activity.getString(R.string.label_login)

        rule.onNodeWithText("Name").performTextInput("TestName")
        rule.onNodeWithText("Surname").performTextInput("TestSurname")
        rule.onNodeWithText("Email").performTextInput("email@gmail.com")
        rule.onNodeWithText("Password").performTextInput("123123123")
        rule.onNodeWithText("Repeat Password").performTextInput("123123123")

        rule.onNode(
            hasText("Sign up")
            and
            hasClickAction()
        ).performClick()

        rule.onNode(
        hasText("Sign up")
            and
            hasClickAction()
        ).assertExists()
    }

}