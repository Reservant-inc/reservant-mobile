package com.example.reservant_mobile.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.constants.AuthRoutes
import com.example.reservant_mobile.ui.constants.MainRoutes
import com.example.reservant_mobile.ui.theme.AppTheme
import com.example.reservant_mobile.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        var startPoint = String()
        splashScreen.setKeepOnScreenCondition { startPoint.isEmpty() }

        lifecycleScope.launch {
            startPoint = if(LoginViewModel().refreshToken())
                MainRoutes.ACTIVITY_HOME
            else
                AuthRoutes.ACTIVITY_LANDING

            setContent {
                AppTheme {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = startPoint) {
                        composable(route = AuthRoutes.ACTIVITY_LANDING) {
                            LandingActivity(navController = navController)
                        }
                        composable(route = AuthRoutes.ACTIVITY_LOGIN) {
                            LoginActivity(navController = navController)
                        }
                        composable(route = AuthRoutes.ACTIVITY_REGISTER) {
                            RegisterActivity(navController = navController)
                        }
                        composable(route = MainRoutes.ACTIVITY_HOME) {
                            HomeActivity()
                        }

                    }
                }
            }

        }
    }
}