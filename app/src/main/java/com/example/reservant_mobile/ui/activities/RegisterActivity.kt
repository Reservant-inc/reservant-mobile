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
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.CountryPickerView
import com.example.reservant_mobile.ui.components.ErrorResourceText
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.LogoWithReturn
import com.example.reservant_mobile.ui.components.MyDatePickerDialog
import com.example.reservant_mobile.ui.components.UserButton
import com.example.reservant_mobile.ui.viewmodels.RegisterViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterActivity(navController: NavHostController) {

    val registerViewModel = viewModel<RegisterViewModel>()
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var formSent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        LogoWithReturn(navController)

        InputUserInfo(
            inputText = registerViewModel.login.value,
            onValueChange = {
                registerViewModel.login.value = it
                registerViewModel.viewModelScope.launch {
                    delay(1000)
                    registerViewModel.checkLoginUnique()
                }
            },
            label = stringResource(R.string.label_login),
            isError = registerViewModel.isLoginInvalid() || !registerViewModel.isLoginUnique,
            errorText = stringResource(
                if (registerViewModel.getLoginError() != -1)
                    registerViewModel.getLoginError()
                else if (!registerViewModel.isLoginUnique)
                    R.string.error_register_username_taken
                else
                    R.string.error_login_invalid
            ),
            formSent = formSent
        )

        InputUserInfo(
            inputText = registerViewModel.firstName.value,
            onValueChange = { registerViewModel.firstName.value = it },
            label = stringResource(R.string.label_name),
            isError = registerViewModel.isFirstNameInvalid(),
            errorText = stringResource(
                if (registerViewModel.getFirstNameError() != -1)
                    registerViewModel.getFirstNameError()
                else
                    R.string.error_register_invalid_name
            ),
            formSent = formSent
        )

        InputUserInfo(
            inputText = registerViewModel.lastName.value,
            onValueChange = { registerViewModel.lastName.value = it },
            label = stringResource(R.string.label_lastname),
            isError = registerViewModel.isLastNameInvalid(),
            errorText = stringResource(
                if (registerViewModel.getLastNameError() != -1)
                    registerViewModel.getLastNameError()
                else
                    R.string.error_register_invalid_lastname
            ),
            formSent = formSent
        )

        MyDatePickerDialog(onBirthdayChange = { birthday -> registerViewModel.birthday.value = birthday })

        InputUserInfo(
            inputText = registerViewModel.email.value,
            onValueChange = { registerViewModel.email.value = it },
            label = stringResource(R.string.label_email),
            isError = registerViewModel.isEmailInvalid(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            errorText = stringResource(
                if (registerViewModel.getEmailError() != -1)
                    registerViewModel.getEmailError()
                else
                    R.string.error_register_invalid_email
            ),
            formSent = formSent
        )

        InputUserInfo(
            inputText = registerViewModel.phoneNum.value,
            onValueChange = { registerViewModel.phoneNum.value = it },
            label = stringResource(R.string.label_phone),
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
            errorText = stringResource(
                if (registerViewModel.getPhoneError() != -1)
                    registerViewModel.getPhoneError()
                else
                    R.string.error_register_invalid_phone
            ),
            optional = true,
            formSent = formSent
        )


        InputUserInfo(
            inputText = registerViewModel.password.value,
            onValueChange = { registerViewModel.password.value = it },
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = registerViewModel.isPasswordInvalid(),
            errorText = stringResource(
                if (registerViewModel.getPasswordError() != -1)
                    registerViewModel.getPasswordError()
                else
                    R.string.error_register_invalid_password
            ),
            formSent = formSent
        )
        InputUserInfo(
            inputText = registerViewModel.confirmPassword.value,
            onValueChange = { registerViewModel.confirmPassword.value = it },
            label = stringResource(R.string.label_register_repeat_password),
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
            isError = registerViewModel.isConfirmPasswordDiff(),
            errorText = stringResource(R.string.error_register_password_match),
            formSent = formSent
        )

        Spacer(modifier = Modifier.weight(1f))
        
        ErrorResourceText(id = registerViewModel.getToastError(), formSent = formSent)
        
        UserButton(
            onClick = {
                registerViewModel.viewModelScope.launch {
                    isLoading = true
                    formSent = true

                    if (registerViewModel.register()){
                        navController.navigate("home")
                    }

                    isLoading = false
                }
            },
            label = stringResource(R.string.label_signup)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterActivity(rememberNavController())
}