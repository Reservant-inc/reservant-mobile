package com.example.reservant_mobile.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.constants.MainRoutes
import com.example.reservant_mobile.ui.constants.RestaurantManagementRoutes
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
                MainRoutes.ACTIVITY_LANDING


            setContent {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = startPoint) {
                    composable(route = MainRoutes.ACTIVITY_LANDING) {
                        LandingActivity(navController = navController)
                    }
                    composable(route = MainRoutes.ACTIVITY_LOGIN) {
                        LoginActivity(navController = navController)
                    }
                    composable(route = MainRoutes.ACTIVITY_REGISTER) {
                        RegisterActivity(navController = navController)
                    }
                    composable(route = MainRoutes.ACTIVITY_HOME) {
                        HomeActivity(navController = navController)
                    }
                    composable(route = RestaurantManagementRoutes.ACTIVITY_MANAGE) {
                        RestaurantManagementActivity(navController = navController)
                    }

                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    //preview if needed
}