package reservant_mobile.activities

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import reservant_mobile.ui.activities.LoginActivity

@OptIn(ExperimentalTestApi::class)
class LoginActivityTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var loginLabel: String
    private lateinit var passwordLabel: String
    private lateinit var signInLabel: String
    private lateinit var error: String

    private lateinit var loginField: SemanticsMatcher
    private lateinit var passwordField: SemanticsMatcher
    private lateinit var signInButton: SemanticsMatcher


    @Before
    fun setupLoginActivityNavHost() {
        rule.setContent {
            LoginActivity(navController = rememberNavController())
        }
        this.loginLabel = rule.activity.getString(R.string.label_login)
        this.passwordLabel = rule.activity.getString(R.string.label_password)
        this.signInLabel = rule.activity.getString(R.string.label_login_action)
        this.error = rule.activity.getString(R.string.error_unauthorized_access)

        this.loginField = hasSetTextAction() and hasText(loginLabel)
        this.passwordField = hasSetTextAction() and hasText(passwordLabel)
        this.signInButton = hasClickAction() and hasText(signInLabel)
    }

    @Test
    fun enterEmpty_showError() {

        rule.onNode(loginField).performTextInput("")
        rule.onNode(passwordField).performTextInput("")
        rule.onNode(signInButton).performClick()

        rule.waitUntil(timeoutMillis = 5000) {
            rule.onAllNodes(signInButton).fetchSemanticsNodes().size == 1
        }

    }

    @Test
    fun enterInvalidUsername_showError() {
        rule.onNode(loginField).performTextInput("invalid")
        rule.onNode(passwordField).performTextInput("Pa${'$'}${'$'}w0rd")
        rule.onNode(signInButton).performClick()

        // test loading circle
        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()

        rule.waitUntil(timeoutMillis = 5000) {
            rule.onAllNodes(signInButton).fetchSemanticsNodes().size == 1
        }

    }
    @Test
    fun enterInvalidPassword_showError() {
        rule.onNode(loginField).performTextInput("john@doe.pl")
        rule.onNode(passwordField).performTextInput("invalid")
        rule.onNode(signInButton).performClick()

        // test loading circle
        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()

        rule.waitUntil(timeoutMillis = 5000) {
            rule.onAllNodes(signInButton).fetchSemanticsNodes().size == 1
        }

    }

    @Test
    fun enterValidData() {
        rule.onNode(loginField).performTextInput("john@doe.pl")
        rule.onNode(passwordField).performTextInput("Pa${'$'}${'$'}w0rd")
        rule.onNode(signInButton).performClick()

        // test loading circle
        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()

        rule.waitUntil(timeoutMillis = 5000) {
            rule.onAllNodes(signInButton).fetchSemanticsNodes().isEmpty()
        }

    }
}