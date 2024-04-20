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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.LogoWithReturn
import com.example.reservant_mobile.ui.components.ShowErrorToast
import com.example.reservant_mobile.ui.components.UserButton
import com.example.reservant_mobile.ui.constants.MainRoutes
import com.example.reservant_mobile.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginActivity(navController: NavHostController) {

    val loginViewModel = viewModel<LoginViewModel>()
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var formSent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoWithReturn(navController)

        InputUserInfo(
            inputText = loginViewModel.login.value,
            onValueChange = {
                loginViewModel.login.value = it
                formSent = false
            },
            label = stringResource(R.string.label_login),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = loginViewModel.result.isError && formSent,
            formSent = formSent
        )

        InputUserInfo(
            inputText = loginViewModel.password.value,
            onValueChange = {
                loginViewModel.password.value = it
                formSent = false
            },
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
                        contentDescription = stringResource(R.string.label_password_visibility)
                    )
                }
            },
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            isError = loginViewModel.result.isError && formSent,
            formSent = formSent
        )


        UserButton(onClick = {
            loginViewModel.viewModelScope.launch {
                isLoading = true
                formSent = true

                if (loginViewModel.login()){
                    navController.navigate(MainRoutes.ACTIVITY_HOME)
                }

                isLoading = false

            }
        }, label = stringResource(R.string.label_login_action), isLoading = isLoading)

        ShowErrorToast(context = LocalContext.current, id = loginViewModel.getToastError())

        Spacer(modifier = Modifier.weight(1f))

        UserButton(onClick = { if (!isLoading) navController.navigate(MainRoutes.ACTIVITY_REGISTER) },
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
