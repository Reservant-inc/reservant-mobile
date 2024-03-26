package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.ErrorResourceText
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.LogoWithReturn
import com.example.reservant_mobile.ui.components.UserButton
import com.example.reservant_mobile.ui.viewmodels.LoginViewModel
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch

@Composable
fun LoginActivity(navController: NavHostController) {

    val loginViewModel = viewModel<LoginViewModel>()
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorResourceId by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp), // TODO: resource
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoWithReturn(navController)

        InputUserInfo(
            inputText = loginViewModel.login,
            onValueChange = { loginViewModel.login = it },
            label = stringResource(R.string.label_login),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = errorResourceId != -1
        )

        InputUserInfo(
            inputText = loginViewModel.password,
            onValueChange = { loginViewModel.password = it },
            label = stringResource(R.string.label_password),
            leadingIcon = {
                IconButton(onClick = {
                    isPasswordVisible = !isPasswordVisible
                }) {
                    Icon(
                        imageVector = if (isPasswordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff,
                        contentDescription = "Password Visibility" // TODO: review
                    )
                }
            },
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            isError = errorResourceId != -1
        )


        UserButton(onClick = {
            loginViewModel.viewModelScope.launch {
                isLoading = true
                errorResourceId = -1

                val loginCode = loginViewModel.login()

                if (loginCode == -1){
                    //navigate to next screen
                }

                errorResourceId = loginCode
                isLoading = false

            }
        }, label = stringResource(R.string.label_signin), isLoading = isLoading)

        ErrorResourceText(id = errorResourceId)

        Spacer(modifier = Modifier.weight(1f))

        UserButton(onClick = { if (!isLoading) navController.navigate("register") },
            label = stringResource(R.string.label_signup))

        UserButton(onClick = { if (!isLoading) return@UserButton /* Handle Password Recovery */ },
            label = stringResource(R.string.label_password_forgot))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogin() {
    LoginActivity(rememberNavController())
}
