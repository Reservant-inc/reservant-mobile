package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.ButtonComponent
import com.example.reservant_mobile.ui.constants.MainRoutes

@Composable
fun RestaurantOwnerProfileActivity(navController: NavController){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ButtonComponent(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            label = stringResource(id = R.string.label_register_restaurant),
            onClick = { navController.navigate(MainRoutes.ACTIVITY_REGISTER_RESTAURANT) },
        )
    }
}

@Composable
@Preview
fun Preview(){
    RestaurantOwnerProfileActivity(navController = rememberNavController())
}