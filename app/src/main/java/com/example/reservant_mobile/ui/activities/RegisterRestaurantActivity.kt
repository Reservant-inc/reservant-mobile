package com.example.reservant_mobile.ui.activities

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RestaurantMenu
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
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
import com.example.reservant_mobile.ui.constants.MainRoutes
import com.example.reservant_mobile.ui.constants.RegisterRestaurantRoutes
import com.example.reservant_mobile.ui.viewmodels.RegisterRestaurantViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RegisterRestaurantActivity(navControllerHome: NavHostController) {

    val registerRestaurantViewModel = viewModel<RegisterRestaurantViewModel>()
    val navController = rememberNavController()
    var isLoading by remember { mutableStateOf(false) }
    var formSent by remember { mutableStateOf(false) }
    var selectedGroup = registerRestaurantViewModel.selectedGroup
    var groups = registerRestaurantViewModel.groups
    val context = LocalContext.current

    var showTagDialog by remember { mutableStateOf(false) }

    registerRestaurantViewModel.viewModelScope.launch {
        registerRestaurantViewModel.getGroups()
        registerRestaurantViewModel.getTags()
    }

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
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                Spacer(modifier = Modifier.padding(top = 8.dp))

                IconWithHeader(
                    icon = Icons.Rounded.RestaurantMenu,
                    text = stringResource(R.string.label_new_restaurant).replace(" ", "\n"),
                )

                ProgressBar(currentStep = 1)

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
                    label = stringResource(R.string.label_restaurant_type),
                    itemsList = options,
                    onOptionSelected = { registerRestaurantViewModel.restaurantType.value = it },
                    isError = registerRestaurantViewModel.isRestaurantTypeInvalid(),
                    errorText = stringResource(
                        if (registerRestaurantViewModel.getRestaurantTypeError() != -1)
                            registerRestaurantViewModel.getRestaurantTypeError()
                        else
                            R.string.error_registerRestaurant_invalid_restaurantType
                    ),
                    formSent = formSent
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

                ShowErrorToast(
                    context = LocalContext.current,
                    id = registerRestaurantViewModel.getToastError(registerRestaurantViewModel.result)
                )

                ButtonComponent(
                    label = stringResource(id = R.string.label_next),
                    onClick = {
                        registerRestaurantViewModel.viewModelScope.launch {
                            isLoading = true

                            val result = registerRestaurantViewModel.validateFirstStep(context)

                            if (result) {
                                navController.navigate(RegisterRestaurantRoutes.ACTIVITY_FILES)
                            }

                            isLoading = false
                        }
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

                Spacer(modifier = Modifier.height(30.dp))
                IconWithHeader(
                    icon = Icons.Rounded.RestaurantMenu,
                    text = stringResource(R.string.label_new_restaurant).replace(" ", "\n"),
                )

                ProgressBar(currentStep = 2)

                Spacer(modifier = Modifier.height(30.dp))

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_consent),
                    onFilePicked = { file ->
                        registerRestaurantViewModel.businessPermission.value = file.toString();
                    },
                    context = context,
                    optional = false,
                    isError = registerRestaurantViewModel.isBusinessPermissionInvalid(context),
                    errorText = stringResource(
                        if (registerRestaurantViewModel.getBusinessPermissionError() != -1)
                            registerRestaurantViewModel.getBusinessPermissionError()
                        else
                            R.string.error_registerRestaurant_invalid_file
                    ),
                    formSent = formSent
                )

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_ownerId),
                    onFilePicked = { file ->
                        registerRestaurantViewModel.idCard.value = file.toString();
                    },
                    context = context,
                    optional = false,
                    isError = registerRestaurantViewModel.isIdCardInvalid(context),
                    errorText = stringResource(
                        if (registerRestaurantViewModel.getIdCardError() != -1)
                            registerRestaurantViewModel.getIdCardError()
                        else
                            R.string.error_registerRestaurant_invalid_file
                    ),
                    formSent = formSent
                )

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_lease),
                    onFilePicked = { file ->
                        registerRestaurantViewModel.rentalContract.value = file.toString();
                    },
                    context = context,
                    optional = true,
                    isError = registerRestaurantViewModel.isRentalContractInvalid(context),
                    errorText = stringResource(
                        if (registerRestaurantViewModel.getRentalContractError() != -1)
                            registerRestaurantViewModel.getRentalContractError()
                        else
                            R.string.error_registerRestaurant_invalid_file
                    ),
                    formSent = formSent,
                    deletable = true
                )

                InputUserFile(
                    label = stringResource(R.string.label_restaurant_license),
                    onFilePicked = { file ->
                        registerRestaurantViewModel.alcoholLicense.value = file.toString();
                    },
                    context = context,
                    optional = true,
                    isError = registerRestaurantViewModel.isAlcoholLicenseInvalid(context),
                    errorText = stringResource(
                        if (registerRestaurantViewModel.getAlcoholLicenseError() != -1)
                            registerRestaurantViewModel.getAlcoholLicenseError()
                        else
                            R.string.error_registerRestaurant_invalid_file
                    ),
                    formSent = formSent,
                    deletable = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (groups != null) {
                    val newGroups = groups + RestaurantGroupDTO(name = registerRestaurantViewModel.name.value)
                    OutLinedDropdownMenu(
                        selectedOption = selectedGroup?.name
                            ?: stringResource(R.string.label_management_choose_group),
                        itemsList = newGroups.map { it.name },
                        onOptionSelected = { name ->
                            registerRestaurantViewModel.viewModelScope.launch {
                                registerRestaurantViewModel.selectedGroup =
                                    newGroups.find { it.name == name }
                            }
                        },
                        label = stringResource(R.string.label_add_to_group)
                    )
                }

                Spacer(Modifier.height(8.dp))

                ButtonComponent(
                    label = stringResource(R.string.label_register_restaurant),
                    onClick = {
                        registerRestaurantViewModel.viewModelScope.launch {
                            isLoading = true

                            val result = registerRestaurantViewModel.validateSecondStep(context)

                            if (result) {
                                navController.navigate(RegisterRestaurantRoutes.ACTIVITY_DESC);
                            }

                            isLoading = false
                        }
                    }
                )

                ShowErrorToast(
                    context = LocalContext.current,
                    id = registerRestaurantViewModel.getToastError(registerRestaurantViewModel.result2)
                )

            }
        }
        composable(route = RegisterRestaurantRoutes.ACTIVITY_DESC) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                IconWithHeader(
                    icon = Icons.Rounded.RestaurantMenu,
                    text = stringResource(R.string.label_new_restaurant).replace(" ", "\n"),
                )

                ProgressBar(currentStep = 3)

                TagList(tags = registerRestaurantViewModel.selectedTags)
                
                ButtonComponent(onClick = { showTagDialog = true }, label = stringResource(id = R.string.label_choose_tags))

                if (showTagDialog) {
                    TagSelectionScreen(
                        vm = registerRestaurantViewModel,
                        onDismiss = { showTagDialog = false },
                        onTagSelected = { tag, isSelected ->
                            if (isSelected) {
                                registerRestaurantViewModel.selectedTags.add(tag)
                            } else {
                                registerRestaurantViewModel.selectedTags.remove(tag)
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
                            selected = registerRestaurantViewModel.delivery,
                            onClick = { registerRestaurantViewModel.delivery = true }
                        )
                        Text(
                            text = stringResource(id = R.string.label_yes),
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
                            text = stringResource(id = R.string.label_no),
                            modifier = Modifier
                                .clickable { registerRestaurantViewModel.delivery = false }
                                .padding(start = 8.dp)
                                .padding(top = 16.dp)
                        )
                    }
                }

                Column {
                    InputUserFile(
                        label = stringResource(id = R.string.label_restaurant_logo),
                        onFilePicked = { file ->
                            registerRestaurantViewModel.logo.value = file.toString();
                        },
                        context = context,
                        isError = registerRestaurantViewModel.isLogoInvalid(context),
                        errorText = stringResource(
                            if (registerRestaurantViewModel.getIdCardError() != -1)
                                registerRestaurantViewModel.getIdCardError()
                            else
                                R.string.error_registerRestaurant_invalid_file
                        ),
                        formSent = formSent
                    )
                    InputUserInfo(
                        inputText = registerRestaurantViewModel.description.value,
                        onValueChange = { registerRestaurantViewModel.description.value = it },
                        label = stringResource(id = R.string.label_restaurant_description),
                        isError = registerRestaurantViewModel.isDescriptionInvalid(),
                        errorText = stringResource(
                            if (registerRestaurantViewModel.getDescriptionError() != -1)
                                registerRestaurantViewModel.getDescriptionError()
                            else
                                R.string.error_registerRestaurant_invalid_description
                        ),
                        formSent = formSent
                    )
                }


                ShowErrorToast(
                    context = LocalContext.current,
                    id = registerRestaurantViewModel.getToastError(registerRestaurantViewModel.result3)
                )

                ButtonComponent(
                    label = stringResource(id = R.string.label_register_restaurant),
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
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}
