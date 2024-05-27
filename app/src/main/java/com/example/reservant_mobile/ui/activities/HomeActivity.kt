package com.example.reservant_mobile.ui.activities

import RestaurantDetailActivity
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.BottomNavigation
import com.example.reservant_mobile.ui.components.FloatingTabSwitch
import com.example.reservant_mobile.ui.navigation.MainRoutes
import com.example.reservant_mobile.ui.navigation.RegisterRestaurantRoutes
import com.example.reservant_mobile.ui.navigation.RestaurantDetailRoutes
import com.example.reservant_mobile.ui.navigation.RestaurantManagementRoutes
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
            NavHost(navController = innerNavController, startDestination = MainRoutes.Home, modifier = Modifier.padding(it)){
                composable<MainRoutes.Home>{
                    val map: List< Pair<String, @Composable () -> Unit>> = listOf(
                         "Test 1" to {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){
                                Text(text="Page 1")
                            }
                        },
                         "Test 2" to {
                            Column(
                                modifier = Modifier.fillMaxSize()
                                    .background(Color.Blue),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){
                                Text(text="Page 2")
                            }
                        }
                    )


                    FloatingTabSwitch(map)
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
                composable<RestaurantDetailRoutes.Details>{
                    RestaurantDetailActivity(navControllerHome = innerNavController, 1)
                }
            }
        }
    }
}