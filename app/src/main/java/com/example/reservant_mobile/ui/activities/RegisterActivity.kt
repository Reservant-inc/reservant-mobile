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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.ui.components.CountryPickerView
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.Logo
import com.example.reservant_mobile.ui.components.MyDatePickerDialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.Logo
import com.example.reservant_mobile.ui.components.LogoWithReturn
import com.example.reservant_mobile.ui.components.UserButton
import com.example.reservant_mobile.ui.viewmodels.RegisterViewModel

@Composable
fun RegisterActivity(navController: NavHostController) {

    val registerViewModel = viewModel<RegisterViewModel>()
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoWithReturn(navController)

        InputUserInfo(
            inputText = registerViewModel.firstName,
            onValueChange = { registerViewModel.firstName = it },
            label = "Name",
            isError = false
        )
        InputUserInfo(
            inputText = registerViewModel.lastName,
            onValueChange = { registerViewModel.lastName = it },
            label = "Surname",
            isError = false
        )

        MyDatePickerDialog(onBirthdayChange = { birthday -> registerViewModel.birthday = birthday })

        InputUserInfo(
            inputText = registerViewModel.email,
            onValueChange = { registerViewModel.email = it },
            label = "Email",
            isError = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        InputUserInfo(
            inputText = registerViewModel.phoneNum,
            onValueChange = { registerViewModel.phoneNum = it },
            label = "Phone",
            leadingIcon = {
                registerViewModel.mobileCountry?.let {
                    CountryPickerView(
                        countries = registerViewModel.countriesList,
                        selectedCountry = it,
                        onSelection = { selectedCountry ->
                            registerViewModel.mobileCountry = selectedCountry
                        },
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = false,
        )


        InputUserInfo(
            inputText = registerViewModel.password,
            onValueChange = { registerViewModel.password = it },
            label = "Password",
            leadingIcon = {
                IconButton(onClick = {
                    isPasswordVisible = !isPasswordVisible
                }) {
                    Icon(
                        imageVector = if (isPasswordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff,
                        contentDescription = "Password Visibility"
                    )
                }
            },
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = false,

            )
        InputUserInfo(
            inputText = registerViewModel.confirmPassword,
            onValueChange = { registerViewModel.confirmPassword = it },
            label = "Repeat Password",
            leadingIcon = {
                IconButton(onClick = {
                    isPasswordVisible = !isPasswordVisible
                }) {
                    Icon(
                        imageVector = if (isPasswordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff,
                        contentDescription = "Password Visibility"
                    )
                }
            },
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            isError = false,
        )


        Spacer(modifier = Modifier.weight(1f))

        UserButton(
            onClick = { println("REGISTER VALIDATION: " + registerViewModel.validateForm()) },
            label = "Sign up"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterActivity(rememberNavController())
}