package com.example.reservant_mobile.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object{
        const val ACTIVITY_HOME = "home"
        const val ACTIVITY_LANDING = "landing"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        var startPoint = String()
        splashScreen.setKeepOnScreenCondition { startPoint.isEmpty() }

        lifecycleScope.launch {
            startPoint = if(LoginViewModel().refreshToken())
                ACTIVITY_HOME
            else
                ACTIVITY_LANDING

            setContent {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = startPoint) {
                    composable(route = "landing") {
                        LandingActivity(navController = navController)
                    }
                    composable(route = "login") {
                        LoginActivity(navController = navController)
                    }
                    composable(route = "register") {
                        RegisterActivity(navController = navController)
                    }
                    composable(route = "home") {
                        HomeActivity(navController = navController)
                    }
                    composable(route = "register-restaurant") {
                        RegisterRestaurantActivity(navController = navController)
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