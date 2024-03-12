package com.example.reservant_mobile.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {

                composable(route = "home") {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        HomeActivity(navController = navController)
                    }
                }
                composable(route = "login") {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LoginActivity(navController = navController)
                    }
                }
                composable(route = "register") {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        RegisterActivity()
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