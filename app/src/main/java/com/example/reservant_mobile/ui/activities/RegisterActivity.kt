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
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.Logo
import com.example.reservant_mobile.ui.components.UserButton

@Composable
fun RegisterActivity() {
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
            label = "Name",
            isError = false
        )
        InputUserInfo(
            inputText = "",
            onValueChange = {},
            label = "Surname",
            isError = false
        )
        InputUserInfo(
            inputText = "",
            onValueChange = {},
            label = "Birth Date",
            isError = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
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
            label = "Phone number",
            isError = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        InputUserInfo(
            inputText = "",
            onValueChange = {},
            label = "Password",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = false,
        )
        InputUserInfo(
            inputText = "",
            onValueChange = {},
            label = "Repeat Password",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = false,
        )


        Spacer(modifier = Modifier.weight(1f))

        UserButton(onClick = { /* Handle Register */ }, label = "Sign up")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterActivity()
}