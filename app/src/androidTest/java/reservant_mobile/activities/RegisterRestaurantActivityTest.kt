package reservant_mobile.activities

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import reservant_mobile.ui.activities.RegisterActivity


class RegisterRestaurantActivityTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupRegisterActivityNavHost() {
        rule.setContent {
            RegisterActivity(navController = rememberNavController())
        }
    }


    @Test
    fun enterInput() {
        rule.onNodeWithText("Name").performTextInput("Test name")
        rule.onNodeWithText("NIP").performTextInput("1234567890")

        rule.onNodeWithText("Restaurant type").performClick()
        rule.onNodeWithText("Bar").performClick()

        rule.onNodeWithText("Address").performTextInput("Mickiewicza 9")
        rule.onNodeWithText("Postal code").performTextInput("02-234")
        rule.onNodeWithText("City").performTextInput("Warsaw")

        rule.onNode(
            hasText("Register Restaurant")
                    and
                    hasClickAction()
        ).performClick()

        rule.onNode(
            hasText("Register Restaurant")
                    and
                    hasClickAction()
        ).assertExists()
    }

}