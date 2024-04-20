package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.services.RestaurantService
import com.example.reservant_mobile.data.services.UserService
import com.example.reservant_mobile.ui.components.LogoWithReturn
import com.example.reservant_mobile.ui.components.OutLinedDropdownMenu
import com.example.reservant_mobile.ui.components.UserButton
import com.example.reservant_mobile.ui.viewmodels.LoginViewModel
import com.example.reservant_mobile.ui.viewmodels.RestaurantManagementViewModel

@Composable
fun RestaurantManagementActivity(navController: NavHostController) {
    val restaurantManageVM = viewModel<RestaurantManagementViewModel>()
    val restaurants = restaurantManageVM.restaurants
    var selectedRestaurant by remember { mutableStateOf<RestaurantDTO?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.label_management_manage),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (restaurants != null) {
            OutLinedDropdownMenu(
                selectedOption = selectedRestaurant?.name ?: stringResource(R.string.label_management_choose_restaurant),
                itemsList = restaurants.map { it.name },
                onOptionSelected = { name ->
                    selectedRestaurant = restaurants.find { it.name == name }
                },
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        selectedRestaurant?.let { restaurant ->
            RestaurantInfoView(restaurant)
        }
    }
}

@Composable
fun RestaurantInfoView(restaurant: RestaurantDTO) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Name: ${restaurant.name}", style = MaterialTheme.typography.bodyLarge)
        Text("Type: ${restaurant.restaurantType}", style = MaterialTheme.typography.bodyLarge)
        Text("NIP: ${restaurant.nip}", style = MaterialTheme.typography.bodyLarge)
        Text("Address: ${restaurant.address}", style = MaterialTheme.typography.bodyLarge)
        Text("Postal Index: ${restaurant.postalIndex}", style = MaterialTheme.typography.bodyLarge)
        Text("City: ${restaurant.city}", style = MaterialTheme.typography.bodyLarge)
        Text("Provide Delivery: ${if (restaurant.provideDelivery) "Yes" else "No"}", style = MaterialTheme.typography.bodyLarge)
        Text("Description: ${restaurant.description}", style = MaterialTheme.typography.bodyLarge)

        Text("Tags: ${restaurant.tags.joinToString()}", style = MaterialTheme.typography.bodyLarge)
        Text("Number of Tables: ${restaurant.tables.size}", style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(10.dp))
        Text("Statistics placeholder: TU BĘDĄ STATYSTYKI", style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManage() {
    RestaurantManagementActivity(rememberNavController())
}