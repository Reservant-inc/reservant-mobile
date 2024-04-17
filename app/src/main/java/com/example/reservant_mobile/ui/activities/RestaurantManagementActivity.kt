package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.services.UserService
import com.example.reservant_mobile.ui.components.LogoWithReturn
import com.example.reservant_mobile.ui.components.OutLinedDropdownMenu
import com.example.reservant_mobile.ui.components.UserButton

@Composable
fun RestaurantManagementActivity(navController: NavHostController) {
    // TODO: get list of restaurants from viewModel
    val restaurants = listOf("Restaurant 1", "Restaurant 2")
    var selectedRestaurant by remember { mutableStateOf<String?>(null) }

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
            text = stringResource(R.string.label_management_manage),
            style = MaterialTheme.typography.headlineMedium
        )
        
        OutLinedDropdownMenu(
            selectedOption = selectedRestaurant ?: stringResource(R.string.label_management_choose_restaurant),
            itemsList = restaurants,
            onOptionSelected = {restaurant ->
                selectedRestaurant = restaurant
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(bottom = 16.dp)
        )
        
        if(selectedRestaurant != null){
            UserButton(
                label = stringResource(R.string.label_management_edit_local_data),
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(top = 8.dp)
            )
            UserButton(
                label = stringResource(R.string.label_management_manage_menu),
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(top = 8.dp)
            )
            UserButton(
                label = stringResource(R.string.label_management_reviews),
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(top = 8.dp)
            )
            UserButton(
                label = stringResource(R.string.label_management_manage_employees),
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(top = 8.dp)
            )
            UserButton(
                label = stringResource(R.string.label_management_manage_subscription),
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(top = 8.dp)
            )
            UserButton(
                label = stringResource(R.string.label_management_delete_restaurant),
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(top = 8.dp)
            )
        }



    }

}


@Preview(showBackground = true)
@Composable
fun PreviewManage() {
    RestaurantManagementActivity(rememberNavController())
}