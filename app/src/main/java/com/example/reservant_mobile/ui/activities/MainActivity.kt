package com.example.reservant_mobile.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.navigation.AuthRoutes
import com.example.reservant_mobile.ui.navigation.MainRoutes
import com.example.reservant_mobile.ui.theme.AppTheme
import com.example.reservant_mobile.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        var startPoint: Any? = null
        splashScreen.setKeepOnScreenCondition { startPoint == null }

        lifecycleScope.launch {
            startPoint = if(LoginViewModel().refreshToken())
                MainRoutes.Home
            else
                AuthRoutes.Landing

            setContent {
                AppTheme {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = startPoint!!
                    ) {
                        composable<AuthRoutes.Landing> {
                            LandingActivity(navController = navController)
                        }
                        composable<AuthRoutes.Login> {
                            LoginActivity(navController = navController)
                        }
                        composable<AuthRoutes.Register> {
                            RegisterActivity(navController = navController)
                        }
                        composable<MainRoutes.Home> {
                            HomeActivity()
                        }
                    }
                }
            }

        }
    }
}