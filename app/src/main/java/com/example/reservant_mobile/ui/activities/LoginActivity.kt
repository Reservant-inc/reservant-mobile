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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.Logo
import com.example.reservant_mobile.ui.components.LogoWithReturn
import com.example.reservant_mobile.ui.components.UserButton
import com.example.reservant_mobile.ui.viewmodels.LoginViewModel

@Composable
fun LoginActivity(navController: NavHostController) {

    val loginViewModel = viewModel<LoginViewModel>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoWithReturn(navController)

        InputUserInfo(
            inputText = loginViewModel.login,
            onValueChange = { loginViewModel.login = it },
            label = "Email",
            isError = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        InputUserInfo(
            inputText = loginViewModel.password,
            onValueChange = { loginViewModel.password = it },
            label = "Password",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = false,
        )

        UserButton(onClick = { println("LOGIN VALIDATION: "+loginViewModel.validateLogin()) }, label = "Login")

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
