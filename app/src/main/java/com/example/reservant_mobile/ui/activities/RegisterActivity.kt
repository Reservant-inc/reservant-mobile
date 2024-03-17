package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.ui.components.DropdownMenuBox
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.Logo
import com.example.reservant_mobile.ui.components.UserButton
import com.example.reservant_mobile.ui.viewmodels.RegisterViewModel
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

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

        Row (Modifier.fillMaxWidth()) {
            DropdownMenuBox(
                label = "Year",
                itemsList = (1900..LocalDate.now().year-18).map { it.toString() },
                modifier = Modifier.weight(1f),
                onItemSelected = { value ->
                    registerViewModel.yearOfBirth = value
                }
            )
            DropdownMenuBox(
                label = "Month",
                itemsList = (1..12).map { it.toString() },
                modifier = Modifier.weight(1f),
                onItemSelected = { value ->
                    if(registerViewModel.monthOfBirth.length==1){
                        registerViewModel.monthOfBirth = "0$value"
                    }
                    else
                        registerViewModel.monthOfBirth = value
                }
            )


            DropdownMenuBox(
                label = "Day",
                itemsList = (1..YearMonth.of(registerViewModel.yearOfBirth.toInt(), registerViewModel.monthOfBirth.toInt()).lengthOfMonth()).map { it.toString() },
                modifier = Modifier.weight(1f),
                onItemSelected = { value ->
                    if(registerViewModel.dayOfBirth.length==1){
                        registerViewModel.dayOfBirth = "0$value"
                    }
                    else
                        registerViewModel.dayOfBirth = value
                }
            )
        }

        InputUserInfo(
            inputText = registerViewModel.email,
            onValueChange = { registerViewModel.email = it },
            label = "Email",
            isError = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Row (Modifier.fillMaxWidth()) {
            DropdownMenuBox(
                label = "Prefix",
                itemsList = getCountryCodesWithPrefixes(),
                modifier = Modifier.weight(1f),
                onItemSelected = { value ->
                    registerViewModel.prefix = "00" + value.substringAfter(" - ").trim()
                }
            )
            InputUserInfo(
                modifier = Modifier.weight(1f),
                inputText = registerViewModel.number,
                onValueChange = { registerViewModel.number = it },
                label = "Phone number",
                isError = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }
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

fun getCountryCodesWithPrefixes(): List<String> {
    val phoneNumberUtil = PhoneNumberUtil.getInstance()
    val countryCodesWithPrefixes = mutableListOf<String>()

    for (regionCode in phoneNumberUtil.supportedRegions) {
        val countryPrefix = phoneNumberUtil.getCountryCodeForRegion(regionCode).toString()
        val countryName = Locale("", regionCode).getDisplayCountry(Locale.ENGLISH)
        val formattedString = "$countryName - $countryPrefix"
        countryCodesWithPrefixes.add(formattedString)
    }

    return countryCodesWithPrefixes
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterActivity()
}