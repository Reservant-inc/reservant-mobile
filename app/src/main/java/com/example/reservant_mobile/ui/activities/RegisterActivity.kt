package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.InputUserInfo
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
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        var maxWidth = Modifier.fillMaxWidth().padding(vertical = 8.dp)

        InputUserInfo(label = "Name", modifier = maxWidth)
        InputUserInfo(label = "Surname", modifier = maxWidth)
        InputUserInfo(label = "Birth Date", modifier = maxWidth)
        InputUserInfo(label = "Email", modifier = maxWidth)
        InputUserInfo(label = "Password", modifier = maxWidth)
        InputUserInfo(label = "Repeat Password", modifier = maxWidth)


        Spacer(modifier = Modifier.weight(1f))

        UserButton(onClick = { /* Handle Register */ }, label = "Sign up", modifier = maxWidth)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterActivity()
}