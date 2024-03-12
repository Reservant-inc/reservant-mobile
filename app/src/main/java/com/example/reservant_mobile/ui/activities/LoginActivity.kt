package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.UserButton

@Composable
fun LoginActivity(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        var maxWidth = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)

        InputUserInfo(label = "login", modifier = maxWidth)

        InputUserInfo(label = "password", modifier = maxWidth)

        UserButton(onClick = { /* Handle login */ }, label = "login", modifier = maxWidth)

        Spacer(modifier = Modifier.weight(1f))

        UserButton(onClick = { navController.navigate("register") }, label = "Sign up", modifier = maxWidth)

        UserButton(onClick = { /* Handle Password Recovery */ }, label = "Don't remember a password", modifier = maxWidth)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogin() {
    LoginActivity(rememberNavController())
}
