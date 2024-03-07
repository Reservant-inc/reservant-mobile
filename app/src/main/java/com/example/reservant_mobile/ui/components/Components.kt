package com.example.reservant_mobile.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun InputUserInfo(label: String, placeholder: String) {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text(text = label) },
        //placeholder = { Text(text = placeholder) }

    )
}


@Preview(showBackground = true)
@Composable
fun InputPreview(){
    Column {
        InputUserInfo("Login", "Insert login")
        InputUserInfo("Password", "Insert password")
    }
}
