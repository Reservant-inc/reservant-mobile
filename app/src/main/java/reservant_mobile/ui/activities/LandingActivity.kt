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
import reservant_mobile.ui.navigation.EmployeeRoutes
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.theme.AppTheme

@Composable
fun LandingActivity(startDestination : Any? = null, onReturnClick: (() -> Unit)? = null ) {

    val isSystemInDarkMode = isSystemInDarkTheme()

    val darkTheme = remember {
        mutableStateOf(isSystemInDarkMode)
    }

    val sd = startDestination?: AuthRoutes.Landing
    AppTheme (darkTheme = darkTheme.value) {
        val landingNavController = rememberNavController()
        NavHost(
            navController = landingNavController,
            startDestination = sd
        ) {
            composable<AuthRoutes.Landing> {
                MapActivity(isUserLoggedIn = false)
            }
            composable<AuthRoutes.Login> {
                LoginActivity(
                    navController = landingNavController,
                    onReturnClick = onReturnClick
                )
            }
            composable<AuthRoutes.Register> {
                RegisterActivity(
                    navController = landingNavController,
                    onReturnClick = onReturnClick
                )
            }
            composable<MainRoutes.Home> {
                HomeActivity()
            }
            composable<EmployeeRoutes.SelectRestaurant> {
                EmployeeHomeActivity()
            }
        }
    }
}