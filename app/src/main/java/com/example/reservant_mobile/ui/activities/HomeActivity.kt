package com.example.reservant_mobile.ui.activities

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.BottomNavigation
import com.example.reservant_mobile.ui.components.Content
import com.example.reservant_mobile.ui.components.heading

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeActivity(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            BottomNavigation(navController)
        },
        topBar = {
            heading()
        },
        content = {
            Content()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    HomeActivity(rememberNavController())
}