package com.example.reservant_mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
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
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    errorText: String = ""
) {

    Column {
        TextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            value = inputText,
            onValueChange = onValueChange,
            label = { Text(text = label) },
            placeholder = { Text(text = placeholder) },
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions.copy(
                imeAction = if (keyboardOptions.imeAction == ImeAction.Default)
                    ImeAction.Next
                else keyboardOptions.imeAction
            ),
            shape = shape,
            isError = isError,
            singleLine = true
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
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick,
    ) {
        Text(text = label)
    }
}

@Composable
fun Logo(modifier: Modifier = Modifier){
    Image(
        painter = painterResource(id = R.drawable.ic_logo),
        contentDescription = "Logo",
        modifier = modifier.size(120.dp)
    )
}

@Composable
fun LogoWithReturn(navController: NavController = rememberNavController()){
    Box (modifier = Modifier.fillMaxWidth()){
        Button(modifier = Modifier
            .align(Alignment.CenterStart)
            ,onClick = { navController.popBackStack() },
            colors = ButtonColors(
                Color.Transparent, Color.Black,
                Color.Transparent, Color.Black
            )
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "back",
                modifier = Modifier.size(35.dp)
            )
        }
        Logo(modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    LogoWithReturn()
}
