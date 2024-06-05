package com.example.reservant_mobile.ui.activities

import RestaurantDetailActivity
import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.BottomNavigation
import com.example.reservant_mobile.ui.navigation.AuthRoutes
import com.example.reservant_mobile.ui.navigation.MainRoutes
import com.example.reservant_mobile.ui.navigation.RegisterRestaurantRoutes
import com.example.reservant_mobile.ui.navigation.RestaurantRoutes
import com.example.reservant_mobile.ui.navigation.RestaurantManagementRoutes
import com.example.reservant_mobile.ui.theme.AppTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun HomeActivity() {
    val innerNavController = rememberNavController()
    val bottomBarState = remember { (mutableStateOf(true)) }


    val isSystemInDarkMode = isSystemInDarkTheme()

    val darkTheme = remember {
        mutableStateOf(isSystemInDarkMode)
    }

    AppTheme (darkTheme = darkTheme.value) {
        Scaffold(
            bottomBar = {
                 BottomNavigation(
                     navController =  innerNavController,
                     bottomBarState = bottomBarState
                 )
            }
        ){
            NavHost(navController = innerNavController, startDestination = MainRoutes.Home, modifier = Modifier.padding(it)){
                composable<MainRoutes.Home>{
                    MapActivity()
                }
                composable<RestaurantManagementRoutes.Restaurant>{
                    RestaurantManagementActivity()
                }
                composable<RegisterRestaurantRoutes.Register>{
                    RegisterRestaurantActivity(navControllerHome = innerNavController)
                }
                composable<MainRoutes.Profile>{
                    RestaurantOwnerProfileActivity(navController = innerNavController, darkTheme = darkTheme)
                }
                composable<RestaurantRoutes.Details>{
                    RestaurantDetailActivity(1)
                }
                composable<AuthRoutes.Landing>{
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    LandingActivity()
                }
            }
        }
    }
}