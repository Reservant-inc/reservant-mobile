package com.example.reservant_mobile.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "landing") {
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

            }

        }

    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    //preview if needed
}