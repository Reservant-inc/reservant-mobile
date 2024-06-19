package com.example.reservant_mobile.ui.activities

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RestaurantMenu
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantGroupDTO
import com.example.reservant_mobile.ui.components.ButtonComponent
import com.example.reservant_mobile.ui.components.IconWithHeader
import com.example.reservant_mobile.ui.components.InputUserFile
import com.example.reservant_mobile.ui.components.InputUserInfo
import com.example.reservant_mobile.ui.components.OutLinedDropdownMenu
import com.example.reservant_mobile.ui.components.ProgressBar
import com.example.reservant_mobile.ui.components.ShowErrorToast
import com.example.reservant_mobile.ui.components.TagList
import com.example.reservant_mobile.ui.components.TagSelectionScreen
import com.example.reservant_mobile.ui.navigation.MainRoutes
import com.example.reservant_mobile.ui.navigation.RegisterRestaurantRoutes
import com.example.reservant_mobile.ui.viewmodels.RestaurantViewModel
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RegisterRestaurantActivity(
    navControllerHome: NavHostController,
    restaurantId: Int? = null,
    group: RestaurantGroupDTO? = null
) {
    val restaurantViewModel = viewModel<RestaurantViewModel>()
    val navController = rememberNavController()
    var isLoading by remember { mutableStateOf(false) }
    var formSent by remember { mutableStateOf(false) }
    var formSent2 by remember { mutableStateOf(false) }
    var formSent3 by remember { mutableStateOf(false) }
    var selectedGroup = restaurantViewModel.selectedGroup
    var groups = restaurantViewModel.groups
    val context = LocalContext.current
    val maxSize = 1024

    var showTagDialog by remember { mutableStateOf(false) }

    if (restaurantId != null && group != null) {
        restaurantViewModel.viewModelScope.launch {
            restaurantViewModel.assignData(restaurantId, group)
        }
    }

    restaurantViewModel.viewModelScope.launch {
        restaurantViewModel.getGroups()
        restaurantViewModel.getTags()
    }

    NavHost(
        navController = navController,
        startDestination = RegisterRestaurantRoutes.Inputs
    ) {
        composable<RegisterRestaurantRoutes.Inputs> {

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
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                Spacer(modifier = Modifier.padding(top = 8.dp))

                if (restaurantId == null && group == null) {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_new_restaurant).replace(" ", "\n"),
                    )
                } else {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_edit_restaurant).replace(" ", "\n"),
                    )
                }

                ProgressBar(currentStep = 1)

                InputUserInfo(
                    inputText = restaurantViewModel.name.value,
                    onValueChange = { restaurantViewModel.name.value = it },
                    label = stringResource(id = R.string.label_restaurant_name),
                    optional = false,
                    isError = restaurantViewModel.isNameInvalid(),
                    errorText = stringResource(
                        if (restaurantViewModel.getNameError() != -1)
                            restaurantViewModel.getNameError()
                        else
                            R.string.error_registerRestaurant_invalid_name
                    ),
                    formSent = formSent
                )

                InputUserInfo(
                    inputText = restaurantViewModel.nip.value,
                    onValueChange = { restaurantViewModel.nip.value = it },
                    label = stringResource(id = R.string.label_restaurant_nip),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    optional = false,
                    isError = restaurantViewModel.isNipInvalid(),
                    errorText = stringResource(
                        if (restaurantViewModel.getNipError() != -1)
                            restaurantViewModel.getNipError()
                        else
                            R.string.error_registerRestaurant_invalid_nip
                    ),
                    formSent = formSent
                )

                OutLinedDropdownMenu(
                    selectedOption = restaurantViewModel.restaurantType.value,
                    label = stringResource(R.string.label_restaurant_type),
                    itemsList = options,
                    onOptionSelected = { restaurantViewModel.restaurantType.value = it },
                    isError = restaurantViewModel.isRestaurantTypeInvalid(),
                    errorText = stringResource(
                        if (restaurantViewModel.getRestaurantTypeError() != -1)
                            restaurantViewModel.getRestaurantTypeError()
                        else
                            R.string.error_registerRestaurant_invalid_restaurantType
                    ),
                    formSent = formSent
                )

                InputUserInfo(
                    inputText = restaurantViewModel.address.value,
                    onValueChange = { restaurantViewModel.address.value = it },
                    label = stringResource(id = R.string.label_restaurant_address),
                    optional = false,
                    isError = restaurantViewModel.isAddressInvalid(),
                    errorText = stringResource(
                        if (restaurantViewModel.getAdressError() != -1)
                            restaurantViewModel.getAdressError()
                        else
                            R.string.error_registerRestaurant_invalid_adress
                    ),
                    formSent = formSent
                )

                InputUserInfo(
                    inputText = restaurantViewModel.postalCode.value,
                    onValueChange = { restaurantViewModel.postalCode.value = it },
                    label = stringResource(id = R.string.label_restaurant_postal),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    optional = false,
                    isError = restaurantViewModel.isPostalCodeInvalid(),
                    errorText = stringResource(
                        if (restaurantViewModel.getPostalError() != -1)
                            restaurantViewModel.getPostalError()
                        else
                            R.string.error_registerRestaurant_invalid_postal
                    ),
                    formSent = formSent
                )

                InputUserInfo(
                    inputText = restaurantViewModel.city.value,
                    onValueChange = { restaurantViewModel.city.value = it },
                    label = stringResource(id = R.string.label_restaurant_city),
                    optional = false,
                    isError = restaurantViewModel.isCityInvalid(),
                    errorText = stringResource(
                        if (restaurantViewModel.getCityError() != -1)
                            restaurantViewModel.getCityError()
                        else
                            R.string.error_registerRestaurant_invalid_city
                    ),
                    formSent = formSent,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )

                Spacer(modifier = Modifier.height(16.dp))

                ShowErrorToast(
                    context = LocalContext.current,
                    id = restaurantViewModel.getToastError(restaurantViewModel.result)
                )

                ButtonComponent(
                    label = if (restaurantId == null && group == null) stringResource(R.string.label_next) else stringResource(
                        R.string.label_edit_restaurant
                    ),
                    isLoading = isLoading,
                    onClick = {
                        restaurantViewModel.viewModelScope.launch {
                            isLoading = true
                            formSent = true

                            val result = restaurantViewModel.validateFirstStep()

                            if (result) {
                                navController.navigate(RegisterRestaurantRoutes.Files)
                            }

                            isLoading = false
                        }
                    }
                )

            }
        }
        composable<RegisterRestaurantRoutes.Files> {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                Spacer(modifier = Modifier.height(30.dp))
                if (restaurantId == null && group == null) {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_new_restaurant).replace(" ", "\n"),
                    )
                } else {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_edit_restaurant).replace(" ", "\n"),
                    )
                }

                ProgressBar(currentStep = 2)

                Spacer(modifier = Modifier.height(30.dp))

                InputUserFile(
                    label = stringResource(id = R.string.label_restaurant_logo),
                    defaultValue = restaurantViewModel.logo.value,
                    onFilePicked = { file ->
                        restaurantViewModel.logo.value = file.toString();
                    },
                    context = context,
                    isError = restaurantViewModel.isLogoInvalid(context),
                    errorText = stringResource(
                        if (restaurantViewModel.getIdCardError() != -1)
                            restaurantViewModel.getIdCardError()
                        else
                            R.string.error_registerRestaurant_invalid_file, maxSize
                    ),
                    formSent = formSent2
                )

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_consent),
                    defaultValue = restaurantViewModel.businessPermission.value,
                    onFilePicked = { file ->
                        restaurantViewModel.businessPermission.value = file.toString();
                    },
                    context = context,
                    optional = false,
                    isError = restaurantViewModel.isBusinessPermissionInvalid(context),
                    errorText = stringResource(
                        if (restaurantViewModel.getBusinessPermissionError() != -1)
                            restaurantViewModel.getBusinessPermissionError()
                        else
                            R.string.error_registerRestaurant_invalid_file, maxSize
                    ),
                    formSent = formSent2
                )

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_ownerId),
                    defaultValue = restaurantViewModel.idCard.value,
                    onFilePicked = { file ->
                        restaurantViewModel.idCard.value = file.toString();
                    },
                    context = context,
                    optional = false,
                    isError = restaurantViewModel.isIdCardInvalid(context),
                    errorText = stringResource(
                        if (restaurantViewModel.getIdCardError() != -1)
                            restaurantViewModel.getIdCardError()
                        else
                            R.string.error_registerRestaurant_invalid_file, maxSize
                    ),
                    formSent = formSent2
                )

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_lease),
                    defaultValue = restaurantViewModel.rentalContract.value,
                    onFilePicked = { file ->
                        restaurantViewModel.rentalContract.value = file.toString();
                    },
                    context = context,
                    optional = true,
                    isError = restaurantViewModel.isRentalContractInvalid(context),
                    errorText = stringResource(
                        if (restaurantViewModel.getRentalContractError() != -1)
                            restaurantViewModel.getRentalContractError()
                        else
                            R.string.error_registerRestaurant_invalid_file, maxSize
                    ),
                    formSent = formSent2,
                    deletable = true
                )

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_license),
                    defaultValue = restaurantViewModel.alcoholLicense.value,
                    onFilePicked = { file ->
                        restaurantViewModel.alcoholLicense.value = file.toString();
                    },
                    context = context,
                    optional = true,
                    isError = restaurantViewModel.isAlcoholLicenseInvalid(context),
                    errorText = stringResource(
                        if (restaurantViewModel.getAlcoholLicenseError() != -1)
                            restaurantViewModel.getAlcoholLicenseError()
                        else
                            R.string.error_registerRestaurant_invalid_file, maxSize
                    ),
                    formSent = formSent2,
                    deletable = true
                )

                Spacer(Modifier.height(8.dp))

                ShowErrorToast(
                    context = LocalContext.current,
                    id = restaurantViewModel.getToastError(restaurantViewModel.result2)
                )

                ButtonComponent(
                    label = if (restaurantId == null && group == null) stringResource(R.string.label_next) else stringResource(
                        R.string.label_edit_restaurant
                    ),
                    isLoading = isLoading,
                    onClick = {
                        restaurantViewModel.viewModelScope.launch {
                            isLoading = true
                            formSent2 = true

                            if (restaurantViewModel.validateSecondStep(context)) {
                                navController.navigate(RegisterRestaurantRoutes.Description)
                            }

                            isLoading = false
                        }
                    }
                )

            }
        }
        composable<RegisterRestaurantRoutes.Description> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                if (restaurantId == null && group == null) {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_new_restaurant).replace(" ", "\n"),
                    )
                } else {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_edit_restaurant).replace(" ", "\n"),
                    )
                }

                ProgressBar(currentStep = 3)

                TagList(
                    tags = restaurantViewModel.selectedTags,
                    onRemoveTag = { tag ->
                        restaurantViewModel.selectedTags =
                            restaurantViewModel.selectedTags.filter { it != tag }
                    })

                ButtonComponent(
                    onClick = { showTagDialog = true },
                    label = stringResource(id = R.string.label_choose_tags)
                )

                if (showTagDialog) {
                    TagSelectionScreen(
                        vm = restaurantViewModel,
                        onDismiss = { showTagDialog = false },
                        onTagSelected = { tag, isSelected ->
                            if (isSelected) {
                                restaurantViewModel.selectedTags += tag
                            } else {
                                restaurantViewModel.selectedTags =
                                    restaurantViewModel.selectedTags.filter { it != tag }
                            }
                        }
                    )
                }



                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.label_registerRestaurant_delivery),
                        modifier = Modifier.weight(1f)
                    )

                    Row {
                        RadioButton(
                            selected = restaurantViewModel.delivery,
                            onClick = { restaurantViewModel.delivery = true }
                        )
                        Text(
                            text = stringResource(id = R.string.label_yes),
                            modifier = Modifier
                                .clickable { restaurantViewModel.delivery = true }
                                .padding(end = 8.dp)
                                .padding(top = 16.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        RadioButton(
                            selected = !restaurantViewModel.delivery,
                            onClick = { restaurantViewModel.delivery = false }
                        )
                        Text(
                            text = stringResource(id = R.string.label_no),
                            modifier = Modifier
                                .clickable { restaurantViewModel.delivery = false }
                                .padding(start = 8.dp)
                                .padding(top = 16.dp)
                        )
                    }
                }

                Column {
                    InputUserInfo(
                        inputText = restaurantViewModel.description.value,
                        onValueChange = { restaurantViewModel.description.value = it },
                        label = stringResource(id = R.string.label_restaurant_description),
                        isError = restaurantViewModel.isDescriptionInvalid(),
                        errorText = stringResource(
                            if (restaurantViewModel.getDescriptionError() != -1)
                                restaurantViewModel.getDescriptionError()
                            else
                                R.string.error_registerRestaurant_invalid_description
                        ),
                        formSent = formSent3
                    )

                    if (groups != null) {
                        val newGroups =
                            groups + RestaurantGroupDTO(name = restaurantViewModel.name.value)
                        OutLinedDropdownMenu(
                            selectedOption = selectedGroup?.name
                                ?: "",
                            itemsList = newGroups.map { it.name },
                            onOptionSelected = { name ->
                                restaurantViewModel.viewModelScope.launch {
                                    restaurantViewModel.selectedGroup =
                                        newGroups.find { it.name == name }
                                }
                            },
                            label = stringResource(R.string.label_add_to_group),
                            isError = restaurantViewModel.isGroupInvalid(),
                            errorText = stringResource(
                                if (restaurantViewModel.getAlcoholLicenseError() != -1)
                                    restaurantViewModel.getAlcoholLicenseError()
                                else
                                    R.string.error_registerRestaurant_invalid_group
                            ),
                            formSent = formSent3
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))


                ShowErrorToast(
                    context = LocalContext.current,
                    id = restaurantViewModel.getToastError(restaurantViewModel.result3)
                )

                if (restaurantId == null && group == null) {
                    ButtonComponent(
                        label = stringResource(id = R.string.label_register_restaurant),
                        isLoading = isLoading,
                        onClick = {
                            restaurantViewModel.viewModelScope.launch {
                                isLoading = true
                                formSent3 = true

                                if (restaurantViewModel.registerRestaurant(context)) {
                                    navControllerHome.navigate(MainRoutes.Home)
                                }

                                isLoading = false
                            }
                        }
                    )
                } else {
                    ButtonComponent(label = stringResource(id = R.string.label_save),
                        isLoading = isLoading,
                        onClick = {
                            restaurantViewModel.viewModelScope.launch {
                                isLoading = true
                                formSent = true

                                if (restaurantViewModel.editRestaurant(context)) {
                                    navControllerHome.navigate(MainRoutes.Home)
                                }

                                isLoading = false
                            }
                        })
                }
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}
