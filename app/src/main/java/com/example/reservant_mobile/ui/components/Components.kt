package com.example.reservant_mobile.ui.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reservant_mobile.R

@Composable
fun InputUserInfo(
    modifier: Modifier = Modifier,
    inputText: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = "",
    visualTransformation:VisualTransformation =  VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    errorText: String = ""

) {

    Column {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            value = inputText,
            onValueChange = onValueChange,
            label = { Text(text = label) },
            placeholder = { Text(text = placeholder) },
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            isError = isError
        )
        if (isError){
            Text(text = errorText)
        }
    }
}

@Composable
fun UserButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String = "",
){
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick,
    ) {
        Text(text = label)
    }
}

@Composable
fun Logo(){
    Image(
        painter = painterResource(id = R.drawable.ic_logo),
        contentDescription = "Logo",
        modifier = Modifier.size(120.dp)
    )
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    //preview if needed
}
