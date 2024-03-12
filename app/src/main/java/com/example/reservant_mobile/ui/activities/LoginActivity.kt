package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.Logo
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
        Logo()

        InputUserInfo(
            inputText = "",
            onValueChange = {},
            label = "Email",
            isError = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        InputUserInfo(
            inputText = "",
            onValueChange = {},
            label = "Password",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = false,
        )

        UserButton(onClick = { /* Handle login */ }, label = "login")

        Spacer(modifier = Modifier.weight(1f))

        UserButton(onClick = { navController.navigate("register") }, label = "Sign up")

        UserButton(onClick = { /* Handle Password Recovery */ }, label = "Don't remember a password")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogin() {
    LoginActivity(rememberNavController())
}
