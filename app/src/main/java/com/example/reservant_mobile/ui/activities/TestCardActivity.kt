package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.reservant_mobile.ui.components.RestaurantCard

@Composable
fun TestCardActivity(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {

        RestaurantCard(
            imageUrl = "url",
            name = "Restauracja 1",
            location = "Lokalizacja"
        )

        RestaurantCard(
            imageUrl = "url",
            name = "Restauracja 2",
            location = "Lokalizacja 2"
        )

    }




}