package com.example.reservant_mobile

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.activities.LoginActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginActivityTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setupLoginActivityNavHost() {
        rule.setContent {
            LoginActivity(navController = rememberNavController())
        }
    }


    @Test
    fun wrongCredentialsReturnErrorCode() {
//        val loginLabel = rule.activity.getString(R.string.label_login)

        rule.onNodeWithText("Login").performTextInput("test")
        rule.onNodeWithText("Password").performTextInput("123123123")

        rule.onNodeWithText("login").performClick()
        rule.onNodeWithText("login").assertExists()
    }
}