package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.Logo
import com.example.reservant_mobile.ui.components.UserButton

@Composable
fun HomeActivity(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo()

        UserButton(onClick = { navController.navigate("login") },
            label = stringResource(R.string.label_signin))

        UserButton(onClick = { navController.navigate("register") },
            label = stringResource(R.string.label_signup))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    HomeActivity(rememberNavController())
}