package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.LogoWithReturn

@Composable
fun RegisterRestaurantActivity(navController: NavHostController) {
    var name = "";
    var nip = "";
    var restaurantType = "";
    var address = "";
    var postalCode = "";
    var city = "";

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        LogoWithReturn(navController)

        InputUserInfo(
            inputText = name,
            onValueChange = { name = it },
            label = "Nazwa",
            optional = false
        )

        InputUserInfo(
            inputText = nip,
            onValueChange = { nip = it },
            label = "NIP",
            optional = false
        )

        InputUserInfo(
            inputText = restaurantType,
            onValueChange = { restaurantType = it },
            label = "Rodzaj restauracji",
            optional = false
        )

        InputUserInfo(
            inputText = address,
            onValueChange = { address = it },
            label = "Adres",
            optional = false
        )

        InputUserInfo(
            inputText = postalCode,
            onValueChange = { postalCode = it },
            label = "Kod pocztowy",
            optional = false
        )

        InputUserInfo(
            inputText = city,
            onValueChange = { city = it },
            label = "Miasto",
            optional = false
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                // Handle registration
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Zarejestruj restaurację")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterRestaurant() {
    RegisterRestaurantActivity(rememberNavController())
}