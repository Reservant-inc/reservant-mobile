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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.LogoWithReturn
import com.example.reservant_mobile.ui.components.RestaurantTypeDropdown

// TODO: labels to resources, fix dropdown menu
@Composable
fun RegisterRestaurantActivity(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var nip by remember { mutableStateOf("") }
    var restaurantType by remember { mutableStateOf("Restaurant") }
    var address by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

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

        RestaurantTypeDropdown(
            selectedOption = restaurantType,
            onOptionSelected = { option ->
                restaurantType = option
            },
            modifier = Modifier.padding(vertical = 8.dp)
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