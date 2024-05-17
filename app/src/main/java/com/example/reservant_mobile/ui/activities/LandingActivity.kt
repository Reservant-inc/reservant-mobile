package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.ButtonComponent
import com.example.reservant_mobile.ui.components.Logo
import com.example.reservant_mobile.ui.navigation.AuthRoutes
import com.example.reservant_mobile.ui.theme.AppTheme

@Composable
fun LandingActivity(navController: NavController) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Logo()

            ButtonComponent(onClick = { navController.navigate(AuthRoutes.Login) }, label = "Login")

            ButtonComponent(onClick = { navController.navigate(AuthRoutes.Register) }, label = "Sign up")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewNew() {
    AppTheme(darkTheme = true) {
        LandingActivity(rememberNavController())
    }
}