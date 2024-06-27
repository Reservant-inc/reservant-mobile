package reservant_mobile.ui.activities

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.Logo
import reservant_mobile.ui.navigation.AuthRoutes
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.theme.AppTheme

@Composable
fun LandingActivity() {

    val isSystemInDarkMode = isSystemInDarkTheme()

    val darkTheme = remember {
        mutableStateOf(isSystemInDarkMode)
    }
    AppTheme (darkTheme = darkTheme.value) {
        val landingNavController = rememberNavController()
        NavHost(
            navController = landingNavController,
            startDestination = AuthRoutes.Landing
        ) {
            composable<AuthRoutes.Landing> {
                Surface {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Logo()

                        ButtonComponent(onClick = { landingNavController.navigate(AuthRoutes.Login) }, label = stringResource(
                            id = R.string.label_login_action
                        ))

                        ButtonComponent(onClick = { landingNavController.navigate(AuthRoutes.Register) }, label = stringResource(
                            id = R.string.label_signup
                        ))
                    }
                }
            }
            composable<AuthRoutes.Login> {
                LoginActivity(navController = landingNavController)
            }
            composable<AuthRoutes.Register> {
                RegisterActivity(navController = landingNavController)
            }
            composable<MainRoutes.Home> {
                HomeActivity()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNew() {
    AppTheme(darkTheme = true) {
        LandingActivity()
    }
}