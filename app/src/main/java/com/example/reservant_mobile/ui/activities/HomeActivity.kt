package com.example.reservant_mobile.ui.activities

import EmployeeManagementActivity
import RestaurantDetailActivity
import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.BottomNavigation
import com.example.reservant_mobile.ui.components.Content
import com.example.reservant_mobile.ui.constants.MainRoutes
import com.example.reservant_mobile.ui.constants.Management
import com.example.reservant_mobile.ui.constants.RegisterRestaurantRoutes
import com.example.reservant_mobile.ui.constants.RestaurantDetailRoutes
import com.example.reservant_mobile.ui.constants.RestaurantManagementRoutes
import com.example.reservant_mobile.ui.theme.AppTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeActivity() {
    val innerNavController = rememberNavController()

    val isSystemInDarkMode = isSystemInDarkTheme()

    val darkTheme = remember {
        mutableStateOf(isSystemInDarkMode)
    }

    AppTheme (darkTheme = darkTheme.value) {
        Scaffold(
            bottomBar = {
                BottomNavigation(innerNavController)
            }
        ){
            NavHost(navController = innerNavController, startDestination = MainRoutes.ACTIVITY_HOME){
                composable(MainRoutes.ACTIVITY_HOME){
                    Content()
                }
                composable(MainRoutes.ACTIVITY_PROFILE){
                    RestaurantOwnerProfileActivity(navController = innerNavController, darkTheme = darkTheme)
                }
                composable(RegisterRestaurantRoutes.ACTIVITY_REGISTER_RESTAURANT){
                    RegisterRestaurantActivity(navControllerHome = innerNavController)
                }
                composable(RestaurantManagementRoutes.ACTIVITY_MANAGE){
                    RestaurantManagementActivity()
                }
                composable(RestaurantDetailRoutes.RESTAURANT_DETAILS){
                    RestaurantDetailActivity(navControllerHome = innerNavController)
                }
            }

        }
    }

}