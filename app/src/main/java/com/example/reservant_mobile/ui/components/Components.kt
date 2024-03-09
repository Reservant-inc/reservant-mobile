package com.example.reservant_mobile.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun InputUserInfo(
    inputText: MutableState<String> = remember { mutableStateOf("") },
    label: String,
    placeholder: String,
    visualTransformation:VisualTransformation =  VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {

    TextField(
        value = inputText.value,
        onValueChange = { inputText.value = it },
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions
    )
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    //preview if needed
}
