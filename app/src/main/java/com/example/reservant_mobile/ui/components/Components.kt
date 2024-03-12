package com.example.reservant_mobile.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun InputUserInfo(
    inputText: MutableState<String> = remember { mutableStateOf("") },
    label: String = "",
    placeholder: String = "",
    visualTransformation:VisualTransformation =  VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
) {

    TextField(
        value = inputText.value,
        onValueChange = { inputText.value = it },
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
    )
}

@Composable
fun UserButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
){
    Button(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(text = label)
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    //preview if needed
}
