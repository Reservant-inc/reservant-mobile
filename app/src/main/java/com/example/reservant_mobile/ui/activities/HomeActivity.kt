package com.example.reservant_mobile.ui.activities

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.BottomNavigation
import com.example.reservant_mobile.ui.components.Content
import com.example.reservant_mobile.ui.constants.MainRoutes
import com.example.reservant_mobile.ui.constants.RegisterRestaurantRoutes

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeActivity() {
    val innerNavController = rememberNavController()

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
                RestaurantOwnerProfileActivity(navController = innerNavController)
            }
            composable(RegisterRestaurantRoutes.ACTIVITY_REGISTER_RESTAURANT){
                RegisterRestaurantActivity()
            }
        }

    }

}