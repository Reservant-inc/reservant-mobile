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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.ButtonComponent
import com.example.reservant_mobile.ui.components.InputUserFile
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.OutLinedDropdownMenu
import com.example.reservant_mobile.ui.components.TagsSelection
import com.example.reservant_mobile.ui.constants.MainRoutes
import com.example.reservant_mobile.ui.constants.RegisterRestaurantRoutes
import com.example.reservant_mobile.ui.viewmodels.RegisterRestaurantViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterRestaurantActivity(navControllerHome: NavHostController) {

    val registerRestaurantViewModel = viewModel<RegisterRestaurantViewModel>()
    val navController = rememberNavController()
    var isLoading by remember { mutableStateOf(false) }
    var formSent by remember { mutableStateOf(false) }
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = RegisterRestaurantRoutes.ACTIVITY_INPUTS
    ) {
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

                InputUserInfo(
                    inputText = registerRestaurantViewModel.name.value,
                    onValueChange = { registerRestaurantViewModel.name.value = it },
                    label = stringResource(id = R.string.label_restaurant_name),
                    optional = false,
                    isError = registerRestaurantViewModel.isNameInvalid(),
                    errorText = stringResource(
                        if (registerRestaurantViewModel.getNameError() != -1)
                            registerRestaurantViewModel.getNameError()
                        else
                            R.string.error_registerRestaurant_invalid_name
                    ),
                    formSent = formSent
                )

                InputUserInfo(
                    inputText = registerRestaurantViewModel.nip.value,
                    onValueChange = { registerRestaurantViewModel.nip.value = it },
                    label = stringResource(id = R.string.label_restaurant_nip),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    optional = false,
                    isError = registerRestaurantViewModel.isNipInvalid(),
                    errorText = stringResource(
                        if (registerRestaurantViewModel.getNipError() != -1)
                            registerRestaurantViewModel.getNipError()
                        else
                            R.string.error_registerRestaurant_invalid_nip
                    ),
                    formSent = formSent
                )

                OutLinedDropdownMenu(
                    selectedOption = registerRestaurantViewModel.restaurantType.value,
                    itemsList = options,
                    onOptionSelected = { registerRestaurantViewModel.restaurantType.value = it }
                )

                InputUserInfo(
                    inputText = registerRestaurantViewModel.address.value,
                    onValueChange = { registerRestaurantViewModel.address.value = it },
                    label = stringResource(id = R.string.label_restaurant_address),
                    optional = false,
                    isError = registerRestaurantViewModel.isAddressInvalid(),
                    errorText = stringResource(
                        if (registerRestaurantViewModel.getAdressError() != -1)
                            registerRestaurantViewModel.getAdressError()
                        else
                            R.string.error_registerRestaurant_invalid_adress
                    ),
                    formSent = formSent
                )

                InputUserInfo(
                    inputText = registerRestaurantViewModel.postalCode.value,
                    onValueChange = { registerRestaurantViewModel.postalCode.value = it },
                    label = stringResource(id = R.string.label_restaurant_postal),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    optional = false,
                    isError = registerRestaurantViewModel.isPostalCodeInvalid(),
                    errorText = stringResource(
                        if (registerRestaurantViewModel.getPostalError() != -1)
                            registerRestaurantViewModel.getPostalError()
                        else
                            R.string.error_registerRestaurant_invalid_postal
                    ),
                    formSent = formSent
                )

                InputUserInfo(
                    inputText = registerRestaurantViewModel.city.value,
                    onValueChange = { registerRestaurantViewModel.city.value = it },
                    label = stringResource(id = R.string.label_restaurant_city),
                    optional = false,
                    isError = registerRestaurantViewModel.isCityInvalid(),
                    errorText = stringResource(
                        if (registerRestaurantViewModel.getCityError() != -1)
                            registerRestaurantViewModel.getCityError()
                        else
                            R.string.error_registerRestaurant_invalid_city
                    ),
                    formSent = formSent
                )

                Spacer(modifier = Modifier.height(16.dp))

                ButtonComponent(
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

                Spacer(modifier = Modifier.height(40.dp))
                Text(text = "Załaduj potrzebne pliki:", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(40.dp))

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_consent),
                    onFilePicked = { file ->
                        registerRestaurantViewModel.businessPermission.value = file.toString();
                    },
                    context = context
                )

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_ownerId),
                    onFilePicked = { file ->
                        registerRestaurantViewModel.idCard.value = file.toString();
                    },
                    context = context
                )

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_lease),
                    onFilePicked = { file ->
                        registerRestaurantViewModel.rentalContract.value = file.toString();
                    },
                    context = context
                )

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_license),
                    onFilePicked = { file ->
                        registerRestaurantViewModel.alcoholLicense.value = file.toString();
                    },
                    context = context
                )

                Spacer(modifier = Modifier.height(80.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ButtonComponent(
                        label = stringResource(R.string.label_register_restaurant),
                        onClick = {
                            navController.navigate(RegisterRestaurantRoutes.ACTIVITY_DESC);
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(16.dp))

                    ButtonComponent(
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Wybierz tagi, które opisują twój lokal",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                TagsSelection(
                    tags = tags,
                    selectedTags = registerRestaurantViewModel.selectedTags,
                    onTagSelected = { tag, isSelected ->
                        if (isSelected) {
                            registerRestaurantViewModel.selectedTags.add(tag)
                        } else {
                            registerRestaurantViewModel.selectedTags.remove(tag)
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
                            selected = registerRestaurantViewModel.delivery,
                            onClick = { registerRestaurantViewModel.delivery = true }
                        )
                        Text(
                            text = "tak",
                            modifier = Modifier
                                .clickable { registerRestaurantViewModel.delivery = true }
                                .padding(end = 8.dp)
                                .padding(top = 16.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        RadioButton(
                            selected = !registerRestaurantViewModel.delivery,
                            onClick = { registerRestaurantViewModel.delivery = false }
                        )
                        Text(
                            text = "nie",
                            modifier = Modifier
                                .clickable { registerRestaurantViewModel.delivery = false }
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
                        onFilePicked = { file ->
                            registerRestaurantViewModel.logo.value = file.toString();
                        },
                        context = context
                    )
                    InputUserInfo(
                        inputText = registerRestaurantViewModel.description.value,
                        onValueChange = { registerRestaurantViewModel.description.value = it },
                        label = stringResource(id = R.string.label_restaurant_description),
                        optional = false
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ButtonComponent(
                    label = "Zapisz",
                    onClick = {
                        registerRestaurantViewModel.viewModelScope.launch {
                            isLoading = true
                            formSent = true

                            if (registerRestaurantViewModel.registerRestaurant(context)) {
                                navControllerHome.navigate(MainRoutes.ACTIVITY_HOME)
                            }

                            isLoading = false
                        }
                    }
                )
            }
        }
    }
}
