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
import com.example.reservant_mobile.ui.components.BirthdayInput
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.Logo
import com.example.reservant_mobile.ui.components.PhoneInput
import com.example.reservant_mobile.ui.components.UserButton
import com.example.reservant_mobile.ui.viewmodels.RegisterViewModel

@Composable
fun RegisterActivity() {

    val registerViewModel = viewModel<RegisterViewModel>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo()

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

        BirthdayInput(
            dateOfBirth = registerViewModel.dateOfBirth,
            onYearChange = { year ->
                registerViewModel.dateOfBirth.yearOfBirth = year
            },
            onMonthChange = { month ->
                registerViewModel.dateOfBirth.monthOfBirth = month
            },
            onDayChange = { day ->
                registerViewModel.dateOfBirth.dayOfBirth = day
            }
        )

        InputUserInfo(
            inputText = registerViewModel.email,
            onValueChange = { registerViewModel.email = it },
            label = "Email",
            isError = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        PhoneInput(
            phoneNumber = registerViewModel.phoneNumber,
            onPrefixChange = { prefix ->
                registerViewModel.phoneNumber.prefix = prefix
            },
            onNumberChange = { number ->
                registerViewModel.phoneNumber.number = number
            },)

        InputUserInfo(
            inputText = registerViewModel.password,
            onValueChange = { registerViewModel.password = it },
            label = "Password",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = false,
        )
        InputUserInfo(
            inputText = registerViewModel.confirmPassword,
            onValueChange = { registerViewModel.confirmPassword = it },
            label = "Repeat Password",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
    RegisterActivity()
}