package com.example.reservant_mobile.ui.activities

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun HomeActivity(navController: NavHostController) {
    Button(onClick = {
        navController.navigate("login")
    }) {
        Text(text = "")
    }
    Button(onClick = {
        navController.navigate("register")
    }) {
        Text(text = "")
    }
}