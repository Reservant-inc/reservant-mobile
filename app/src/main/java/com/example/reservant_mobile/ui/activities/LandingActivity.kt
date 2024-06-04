package com.example.reservant_mobile.ui.activities

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.ButtonComponent
import com.example.reservant_mobile.ui.components.Logo
import com.example.reservant_mobile.ui.navigation.AuthRoutes
import com.example.reservant_mobile.ui.navigation.MainRoutes
import com.example.reservant_mobile.ui.theme.AppTheme

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

                        ButtonComponent(onClick = { landingNavController.navigate(AuthRoutes.Login) }, label = "Login")

                        ButtonComponent(onClick = { landingNavController.navigate(AuthRoutes.Register) }, label = "Sign up")
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