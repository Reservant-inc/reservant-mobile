package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.reservant_mobile.ui.components.CountryPickerView
import com.example.reservant_mobile.ui.components.ErrorResourceText
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.LogoWithReturn
import com.example.reservant_mobile.ui.components.MyDatePickerDialog
import com.example.reservant_mobile.ui.components.UserButton
import com.example.reservant_mobile.ui.viewmodels.RegisterViewModel
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch

@Composable
fun RegisterActivity(navController: NavHostController) {

    val registerViewModel = viewModel<RegisterViewModel>()
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorResourceId by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) // TODO: resource
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        LogoWithReturn(navController)

        InputUserInfo(
            inputText = registerViewModel.login,
            onValueChange = { registerViewModel.login = it },
            label = stringResource(R.string.label_login),
            isError = registerViewModel.isLoginInvalid(),
            errorText = stringResource(R.string.error_login_invalid)
        )

        InputUserInfo(
            inputText = registerViewModel.firstName,
            onValueChange = { registerViewModel.firstName = it },
            label = stringResource(R.string.label_name),
            isError = registerViewModel.isFirstNameInvalid(),
            errorText = stringResource(R.string.error_register_invalid_name)
        )

        InputUserInfo(
            inputText = registerViewModel.lastName,
            onValueChange = { registerViewModel.lastName = it },
            label = "Surname",
            isError = registerViewModel.isLastNameInvalid(), // TODO: lastName or surname?
            errorText = "Invalid surname"
        )

        MyDatePickerDialog(onBirthdayChange = { birthday -> registerViewModel.birthday = birthday })

        InputUserInfo(
            inputText = registerViewModel.email,
            onValueChange = { registerViewModel.email = it },
            label = stringResource(R.string.label_email),
            isError = registerViewModel.isEmailInvalid(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            errorText = stringResource(R.string.error_register_invalid_email)
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
            isError = registerViewModel.isPhoneInvalid(),
            errorText = "Invalid phone number",
            optional = true
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
            isError = registerViewModel.isPasswordInvalid(),
            errorText = "Invalid password"

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
            isError = registerViewModel.isConfirmPasswordDiff(),
            errorText = "Passwords must match"
        )

        Spacer(modifier = Modifier.weight(1f))

        ErrorResourceText(id = errorResourceId)
        
        UserButton(
            onClick = {
                registerViewModel.viewModelScope.launch {
                    isLoading = true

                    val registerCode = registerViewModel.register()

                    if (registerCode == -1){
                        //navigate to next screen
                    }

                    errorResourceId = registerCode
                    isLoading = false
                }
            },
            label = "Sign up"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterActivity(rememberNavController())
}