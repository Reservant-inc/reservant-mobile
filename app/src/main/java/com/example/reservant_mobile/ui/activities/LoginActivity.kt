package com.example.reservant_mobile.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.reservant_mobile.ui.components.InputUserInfo


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column {
                InputUserInfo("Login", "Insert login")
                InputUserInfo("Password", "Insert password")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Text("Android")
}