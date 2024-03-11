package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reservant_mobile.R

@Composable
fun RegisterActivity() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        var emailText by remember { mutableStateOf("") }
        var passwordText by remember { mutableStateOf("") }
        var nameText by remember { mutableStateOf("") }
        var surnameText by remember { mutableStateOf("") }
        var phoneText by remember { mutableStateOf("") }
        var birthdateText by remember { mutableStateOf("") }
        var passwordrepeatText by remember { mutableStateOf("") }

        TextField(
            value = nameText,
            onValueChange = { nameText = it },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TextField(
            value = surnameText,
            onValueChange = { surnameText = it },
            label = { Text("Surname") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TextField(
            value = phoneText,
            onValueChange = { phoneText = it },
            label = { Text("Phone") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TextField(
            value = birthdateText,
            onValueChange = { birthdateText = it },
            label = { Text("Birth Date") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TextField(
            value = emailText,
            onValueChange = { emailText = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TextField(
            value = passwordrepeatText,
            onValueChange = { passwordrepeatText = it },
            label = { Text("Repeat Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { /* Handle Register */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Sign up")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterActivity()
}