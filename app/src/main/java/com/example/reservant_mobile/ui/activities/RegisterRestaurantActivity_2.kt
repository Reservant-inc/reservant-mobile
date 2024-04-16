package com.example.reservant_mobile.ui.activities

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.InputUserFile
import com.example.reservant_mobile.ui.components.LogoWithReturn
import com.example.reservant_mobile.ui.components.UserButton

@Composable
fun RegisterRestaurantActivity_2(navController: NavHostController) {


    var lease by remember { mutableStateOf<Uri?>(null) }
    var license by remember { mutableStateOf<Uri?>(null) }
    var consent by remember { mutableStateOf<Uri?>(null) }
    var idCard by remember { mutableStateOf<Uri?>(null) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        LogoWithReturn(navController)

        InputUserFile(
            label = stringResource(R.string.label_restaurant_lease),
            onFilePicked = { file ->
                lease = file;
            }
        )

        InputUserFile(
            label = stringResource(R.string.label_restaurant_license),
            onFilePicked = { file ->
                license = file;
            }
        )

        InputUserFile(
            label = stringResource(R.string.label_restaurant_consent),
            onFilePicked = { file ->
                consent = file;
            }
        )

        InputUserFile(
            label = stringResource(R.string.label_restaurant_ownerId),
            onFilePicked = { file ->
                idCard = file;
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            UserButton(
                label = stringResource(R.string.label_register_restaurant),
                onClick = {
                    // Handling registration
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(16.dp))

            UserButton(
                label = stringResource(R.string.label_add_to_group),
                onClick = {
                    // Adding to group
                },
                modifier = Modifier.weight(1f)
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterRestaurant_2() {
    RegisterRestaurantActivity_2(rememberNavController())
}