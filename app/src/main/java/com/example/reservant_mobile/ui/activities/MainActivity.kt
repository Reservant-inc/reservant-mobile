package com.example.reservant_mobile.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
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

        var isLoading = true
        var startPoint: @Composable ()->Unit
        splashScreen.setKeepOnScreenCondition { isLoading }

        lifecycleScope.launch {
             startPoint = if(LoginViewModel().refreshToken()) {
                 {HomeActivity()}
             } else {
                 {LandingActivity()}
             }

            setContent {
                AppTheme {
                    isLoading=false
                    startPoint()
                }
            }

        }
    }
}