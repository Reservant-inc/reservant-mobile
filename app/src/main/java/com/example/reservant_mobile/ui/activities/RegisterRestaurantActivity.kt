package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.InputUserFile
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.LogoWithReturn
import com.example.reservant_mobile.ui.components.OutLinedDropdownMenu
import com.example.reservant_mobile.ui.components.TagsSelection
import com.example.reservant_mobile.ui.components.UserButton
import com.example.reservant_mobile.ui.constants.RegisterRestaurantRoutes
import com.example.reservant_mobile.ui.viewmodels.RegisterRestaurantViewModel

@Composable
fun RegisterRestaurantActivity() {

    val registerRestaurantViewModel = viewModel<RegisterRestaurantViewModel>()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RegisterRestaurantRoutes.ACTIVITY_INPUTS) {
        composable(route = RegisterRestaurantRoutes.ACTIVITY_INPUTS) {

            val options = listOf(
                stringResource(R.string.label_restaurant_type_restaurant),
                stringResource(R.string.label_restaurant_type_bar),
                stringResource(R.string.label_restaurant_type_cafe)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                LogoWithReturn(navController)

                InputUserInfo(
                    inputText = registerRestaurantViewModel.name,
                    onValueChange = { registerRestaurantViewModel.name = it },
                    label = stringResource(id = R.string.label_restaurant_name),
                    optional = false
                )

                InputUserInfo(
                    inputText = registerRestaurantViewModel.nip,
                    onValueChange = { registerRestaurantViewModel.nip = it },
                    label = stringResource(id = R.string.label_restaurant_nip),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    optional = false
                )

                OutLinedDropdownMenu(
                    selectedOption = registerRestaurantViewModel.restaurantType,
                    itemsList = options,
                    onOptionSelected = { registerRestaurantViewModel.restaurantType = it }
                )

                InputUserInfo(
                    inputText = registerRestaurantViewModel.address,
                    onValueChange = { registerRestaurantViewModel.address = it },
                    label = stringResource(id = R.string.label_restaurant_address),
                    optional = false
                )

                InputUserInfo(
                    inputText = registerRestaurantViewModel.postalCode,
                    onValueChange = { registerRestaurantViewModel.postalCode = it },
                    label = stringResource(id = R.string.label_restaurant_postal),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    optional = false
                )

                InputUserInfo(
                    inputText = registerRestaurantViewModel.city,
                    onValueChange = { registerRestaurantViewModel.city = it },
                    label = stringResource(id = R.string.label_restaurant_city),
                    optional = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                UserButton(
                    label = "Next",
                    onClick = {
                        //TODO: send validate inputs
                        navController.navigate(RegisterRestaurantRoutes.ACTIVITY_FILES)
                    }
                )

            }
        }
        composable(route = RegisterRestaurantRoutes.ACTIVITY_FILES) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                LogoWithReturn(navController)
                Spacer(modifier = Modifier.height(40.dp))
                Text(text = "Załaduj potrzebne pliki:", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(40.dp))

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_consent),
                    onFilePicked = { file ->
                        registerRestaurantViewModel.consentUri = file.toString();
                    }
                )

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_ownerId),
                    onFilePicked = { file ->
                        registerRestaurantViewModel.idCardUri = file.toString();
                    }
                )

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_lease),
                    onFilePicked = { file ->
                        registerRestaurantViewModel.leaseUri = file.toString();
                    }
                )

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_license),
                    onFilePicked = { file ->
                        registerRestaurantViewModel.licenseUri = file.toString();
                    }
                )

                Spacer(modifier = Modifier.height(80.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    UserButton(
                        label = stringResource(R.string.label_register_restaurant),
                        onClick = {
                            navController.navigate(RegisterRestaurantRoutes.ACTIVITY_DESC);
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(16.dp))

                    UserButton(
                        label = stringResource(R.string.label_add_to_group),
                        onClick = {
                            navController.navigate(RegisterRestaurantRoutes.ACTIVITY_DESC);
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

            }
        }
        composable(route = RegisterRestaurantRoutes.ACTIVITY_DESC) {
            // TODO: resources
            val tags = listOf("na miejscu", "na wynos", "azjatyckie", "włoskie", "tag1", "tag2")
            val selectedTags = remember { mutableStateListOf<String>() }
            var delivery by remember { mutableStateOf(true) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Wybierz tagi, które opisują twój lokal", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))

                TagsSelection(
                    tags = tags,
                    selectedTags = selectedTags,
                    onTagSelected = { tag, isSelected ->
                        if (isSelected) {
                            selectedTags.add(tag)
                        } else {
                            selectedTags.remove(tag)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dostawa na naszym pośrednictwem:",
                        modifier = Modifier.weight(1f)
                    )

                    Row {
                        RadioButton(
                            selected = delivery,
                            onClick = { delivery = true }
                        )
                        Text(
                            text = "tak",
                            modifier = Modifier
                                .clickable { delivery = true }
                                .padding(end = 8.dp)
                                .padding(top = 16.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        RadioButton(
                            selected = !delivery,
                            onClick = { delivery = false }
                        )
                        Text(
                            text = "nie",
                            modifier = Modifier
                                .clickable { delivery = false }
                                .padding(start = 8.dp)
                                .padding(top = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // File upload and description
                Column {
                    InputUserFile(
                        label = "Logo, zdjęcia lokalu",
                        onFilePicked = {
                            // ...
                        }
                    )
                    TextField(
                        value = "Opis lokalu",
                        onValueChange = { /* Handle description input */ },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                UserButton(
                    label = "Zapisz",
                    onClick = { /* Handle file add */ }
                )
            }
        }
    }
}
