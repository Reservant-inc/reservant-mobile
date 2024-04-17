package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.LogoWithReturn
import com.example.reservant_mobile.ui.components.UserButton

@Composable
fun RestaurantManagementActivity(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        LogoWithReturn(navController)

        Text(
            text = "Nazwa restauracji"
        )

        UserButton(
            label = "Edytuj dane lokalu",
            onClick = { /*TODO*/ }
        )
        UserButton(
            label = "Zarządzaj menu",
            onClick = { /*TODO*/ }
        )
        UserButton(
            label = "Opinie",
            onClick = { /*TODO*/ }
        )
        UserButton(
            label = "Zarzadzaj pracownikami",
            onClick = { /*TODO*/ }
        )
        UserButton(
            label = "Zarzadzaj subskrypcja",
            onClick = { /*TODO*/ }
        )
        UserButton(
            label = "Usuń lokal",
            onClick = { /*TODO*/ }
        )


    }






}


@Preview(showBackground = true)
@Composable
fun PreviewManage() {
    RestaurantManagementActivity(rememberNavController())
}