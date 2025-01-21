package reservant_mobile.ui.activities

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.RestaurantGroupDTO
import reservant_mobile.data.utils.isFileSizeInvalid
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.ComboBox
import reservant_mobile.ui.components.FormFileInput
import reservant_mobile.ui.components.FormInput
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.OpeningHourDayInput
import reservant_mobile.ui.components.ProgressBar
import reservant_mobile.ui.components.SecondaryButton
import reservant_mobile.ui.components.ShowErrorToast
import reservant_mobile.ui.components.TagList
import reservant_mobile.ui.components.TagSelectionScreen
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.RegisterRestaurantRoutes
import reservant_mobile.ui.viewmodels.RestaurantViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RegisterRestaurantActivity(
    onReturnClick: () -> Unit,
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
    var addGroup by remember { mutableStateOf(false) }
    var selectedGroup = restaurantViewModel.selectedToGroup.value ?: restaurantViewModel.selectedGroup
    var groups = restaurantViewModel.groups
    val context = LocalContext.current
    val maxSize = 1024

    var showTagDialog by remember { mutableStateOf(false) }

    if (restaurantId != null && group != null) {
        LaunchedEffect(key1 = Unit) {
            restaurantViewModel.assignData(restaurantId, group)
        }
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

                if (restaurantId == null && group == null) {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_new_restaurant),
                        showBackButton = true,
                        onReturnClick = onReturnClick
                    )
                } else {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_edit_restaurant),
                        showBackButton = true,
                        onReturnClick = onReturnClick
                    )
                }

                ProgressBar(currentStep = 1, maxStep = 4)

                FormInput(
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

                FormInput(
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

                ComboBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = remember {
                        mutableStateOf(false)
                    },
                    value = restaurantViewModel.restaurantType.value,
                    onValueChange = { restaurantViewModel.restaurantType.value = it } ,
                    label = stringResource(R.string.label_restaurant_type),
                    options = options,
                    isError = restaurantViewModel.isRestaurantTypeInvalid(),
                    errorText = stringResource(
                        if (restaurantViewModel.getRestaurantTypeError() != -1)
                            restaurantViewModel.getRestaurantTypeError()
                        else
                            R.string.error_registerRestaurant_invalid_restaurantType
                    )
                )

                FormInput(
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

                FormInput(
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

                FormInput(
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
                    id = restaurantViewModel.getToastError1()
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
                            val resNomi = restaurantViewModel.validateAddress()

                            if (result && resNomi) {
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

                if (restaurantId == null && group == null) {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_new_restaurant),
                        showBackButton = true,
                        onReturnClick = { navController.popBackStack() }
                    )
                } else {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_edit_restaurant),
                        showBackButton = true,
                        onReturnClick = { navController.popBackStack() }
                    )
                }

                ProgressBar(currentStep = 2, maxStep = 4)

                Spacer(modifier = Modifier.height(30.dp))

                FormFileInput(
                    label = stringResource(id = R.string.label_restaurant_logo),
                    defaultValue =
                    if (restaurantViewModel.logo.value == "null")
                        ""
                    else
                        restaurantViewModel.logo.value,
                    onFilePicked = { file ->
                        restaurantViewModel.logo.value = file.toString();
                    },
                    context = context,
                    isError = restaurantViewModel.isLogoInvalid(context),
                    errorText = stringResource(
                        if (restaurantViewModel.getLogoError() != -1) {
                            restaurantViewModel.getLogoError()
                        } else
                            if (isFileSizeInvalid(context, restaurantViewModel.logo.value)) {
                                R.string.error_file_size
                            } else
                                R.string.error_registerRestaurant_invalid_file_photo, maxSize
                    ),
                    formSent = formSent2
                )

                FormFileInput(
                    label = stringResource(id = R.string.label_gallery),
                    defaultValue =
                    if (restaurantViewModel.photos.isNotEmpty())
                        stringResource(id = R.string.label_selected_elements, restaurantViewModel.photos.size)
                    else
                        "",
                    onFilesPicked = { files ->
                        if (files != null) {
                            restaurantViewModel.photos = files.map { it.toString() }
                        }
                    },
                    context = context,
                    isError = restaurantViewModel.arePhotosInvalid(context),
                    errorText = stringResource(
                        if (restaurantViewModel.getPhotosError() != -1) {
                            restaurantViewModel.getPhotosError()
                        } else{
                            if (restaurantViewModel.photos.all { isFileSizeInvalid(context, it) }) {
                                R.string.error_file_size
                            } else
                                R.string.error_registerRestaurant_invalid_file_photo
                        }, maxSize

                    ),
                    formSent = formSent2,
                    deletable = true,
                    optional = true,
                    multipleFiles = true
                )

                FormFileInput(
                    label = stringResource(R.string.label_restaurant_consent),
                    defaultValue =
                    if (restaurantViewModel.businessPermission.value == "null")
                        ""
                    else
                        restaurantViewModel.businessPermission.value,
                    onFilePicked = { file ->
                        restaurantViewModel.businessPermission.value = file.toString();
                    },
                    context = context,
                    optional = false,
                    isError = restaurantViewModel.isBusinessPermissionInvalid(context),
                    errorText = stringResource(
                        if (restaurantViewModel.getBusinessPermissionError() != -1) {
                            restaurantViewModel.getBusinessPermissionError()
                        } else
                            if (isFileSizeInvalid(
                                    context,
                                    restaurantViewModel.businessPermission.value
                                )
                            ) {
                                R.string.error_file_size
                            } else
                                R.string.error_registerRestaurant_invalid_file_pdf, maxSize
                    ),
                    formSent = formSent2
                )

                FormFileInput(
                    label = stringResource(R.string.label_restaurant_ownerId),
                    defaultValue =
                    if (restaurantViewModel.idCard.value == "null")
                        ""
                    else
                        restaurantViewModel.idCard.value,
                    onFilePicked = { file ->
                        restaurantViewModel.idCard.value = file.toString();
                    },
                    context = context,
                    optional = false,
                    isError = restaurantViewModel.isIdCardInvalid(context),
                    errorText = stringResource(
                        if (restaurantViewModel.getIdCardError() != -1) {
                            restaurantViewModel.getIdCardError()
                        } else
                            if (isFileSizeInvalid(context, restaurantViewModel.idCard.value)) {
                                R.string.error_file_size
                            } else
                                R.string.error_registerRestaurant_invalid_file_pdf, maxSize
                    ),
                    formSent = formSent2
                )

                FormFileInput(
                    label = stringResource(R.string.label_restaurant_lease),
                    defaultValue =
                    if (restaurantViewModel.rentalContract.value == "null")
                        ""
                    else
                        restaurantViewModel.rentalContract.value,
                    onFilePicked = { file ->
                        restaurantViewModel.rentalContract.value = file.toString();
                    },
                    context = context,
                    optional = true,
                    isError = restaurantViewModel.isRentalContractInvalid(context),
                    errorText = stringResource(
                        if (restaurantViewModel.getRentalContractError() != -1) {
                            restaurantViewModel.getRentalContractError()
                        } else
                            if (isFileSizeInvalid(
                                    context,
                                    restaurantViewModel.rentalContract.value
                                )
                            ) {
                                R.string.error_file_size
                            } else
                                R.string.error_registerRestaurant_invalid_file_pdf, maxSize
                    ),
                    formSent = formSent2,
                    deletable = true
                )

                FormFileInput(
                    label = stringResource(R.string.label_restaurant_license),
                    defaultValue =
                    if (restaurantViewModel.alcoholLicense.value == "null")
                        ""
                    else
                        restaurantViewModel.alcoholLicense.value,
                    onFilePicked = { file ->
                        restaurantViewModel.alcoholLicense.value = file.toString();
                    },
                    context = context,
                    optional = true,
                    isError = restaurantViewModel.isAlcoholLicenseInvalid(context),
                    errorText = stringResource(
                        if (restaurantViewModel.getAlcoholLicenseError() != -1) {
                            restaurantViewModel.getAlcoholLicenseError()
                        } else
                            if (isFileSizeInvalid(
                                    context,
                                    restaurantViewModel.alcoholLicense.value
                                )
                            ) {
                                R.string.error_file_size
                            } else
                                R.string.error_registerRestaurant_invalid_file_pdf, maxSize
                    ),
                    formSent = formSent2,
                    deletable = true
                )

                Spacer(Modifier.height(8.dp))

                ShowErrorToast(
                    context = LocalContext.current,
                    id = restaurantViewModel.getToastError2()
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
                                navController.navigate(RegisterRestaurantRoutes.OpeningHours)
                            }

                            isLoading = false
                        }
                    }
                )

            }
        }
        composable<RegisterRestaurantRoutes.OpeningHours> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                if (restaurantId == null && group == null) {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_add_opening_hours),
                        showBackButton = true,
                        onReturnClick = { navController.popBackStack() }
                    )
                } else {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_edit_opening_hours),
                        showBackButton = true,
                        onReturnClick = { navController.popBackStack() }
                    )
                }

                ProgressBar(currentStep = 3, maxStep = 4)

                Spacer(modifier = Modifier.height(30.dp))

                restaurantViewModel.openingHours.forEachIndexed { index, pair ->
                    var isOpen by remember {
                        mutableStateOf(pair.first != null && pair.second != null)
                    }

                    val today by remember {
                        mutableStateOf(restaurantViewModel.openingHours[index])
                    }

                    val dayOfWeek by remember {
                        mutableStateOf(LocalDate.now()
                            .with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
                            .plusDays(index.toLong())
                            .format(DateTimeFormatter
                                .ofPattern("EEEE", Locale.getDefault()))
                        )
                    }

                    var isOpeningTimeInvalid by remember {
                        mutableStateOf(restaurantViewModel.isOpeningHoursTimeInvalid(pair))
                    }

                    OpeningHourDayInput(
                        dayOfWeek = dayOfWeek,
                        isOpen = isOpen,
                        onOpenChange = {
                            isOpen = !isOpen

                            restaurantViewModel.openingHours[index] = if (isOpen)
                                "09:00" to "18:00"
                            else
                                null to null

                            isOpeningTimeInvalid = restaurantViewModel.isOpeningHoursTimeInvalid(
                                restaurantViewModel.openingHours[index]
                            )
                        },
                        startTime = pair.first ?: "09:00",
                        onStartTimeChange = {
                            restaurantViewModel.openingHours[index] = it to today.second
                            isOpeningTimeInvalid = restaurantViewModel.isOpeningHoursTimeInvalid(
                                restaurantViewModel.openingHours[index]
                            )
                        },
                        endTime = pair.second ?: "18:00",
                        onEndTimeChange = {
                            restaurantViewModel.openingHours[index] = today.first to it
                            isOpeningTimeInvalid = restaurantViewModel.isOpeningHoursTimeInvalid(
                                restaurantViewModel.openingHours[index]
                            )
                        }
                    )

                    if (isOpen && isOpeningTimeInvalid){
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp),
                            text = stringResource(id = R.string.error_openTimeBeforeCloseTime),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                ButtonComponent(
                    label = if (restaurantId == null && group == null) stringResource(R.string.label_next) else stringResource(
                        R.string.label_edit_restaurant
                    ),
                    onClick = {
                        if (!restaurantViewModel.areOpeningHoursInvalid()){
                            navController.navigate(RegisterRestaurantRoutes.Description)
                        }
                    }
                )

            }
        }
        composable<RegisterRestaurantRoutes.Description> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                if (restaurantId == null && group == null) {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_new_restaurant),
                        showBackButton = true,
                        onReturnClick = { navController.popBackStack() }
                    )
                } else {
                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_edit_restaurant),
                        showBackButton = true,
                        onReturnClick = { navController.popBackStack() }
                    )
                }

                ProgressBar(currentStep = 4, maxStep = 4)

                Spacer(modifier = Modifier.height(30.dp))
                
                if (restaurantViewModel.selectedTags.isEmpty()){
                    Text(
                        text = stringResource(id = R.string.error_no_tags_selected),
                        color = MaterialTheme.colorScheme.error
                    )
                }

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

                    Column(modifier = Modifier.fillMaxWidth()) {
                        var wantDeposit by remember { mutableStateOf(false) }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.label_want_deposit),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Checkbox(
                                checked = wantDeposit,
                                onCheckedChange = { wantDeposit = it }
                            )
                        }

                        if (wantDeposit) {
                            FormInput(
                                inputText = if (restaurantViewModel.deposit == 0.0) ""
                                else restaurantViewModel.deposit.toString(),
                                onValueChange = { value ->
                                    restaurantViewModel.deposit = value.toDoubleOrNull() ?: 0.0
                                },
                                label = stringResource(R.string.label_deposit_amount),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                optional = true
                            )
                        }else{
                            restaurantViewModel.deposit = 0.0
                        }
                    }

                    FormInput(
                        inputText = restaurantViewModel.maxReservationMinutes.value,
                        onValueChange = { restaurantViewModel.maxReservationMinutes.value = it },
                        label = stringResource(id = R.string.label_restaurant_maxReservationDuration),
                        isError = restaurantViewModel.isMaxReservationDurationInvalid(),
                        errorText = if (restaurantViewModel.getReservationDurationError() != -1)
                                stringResource(
                                    id = restaurantViewModel.getReservationDurationError(),
                                    restaurantViewModel.maxReservationMinutes.name,
                                    "30"
                                )
                            else
                                stringResource(id = R.string.error_registerRestaurant_invalid_max_duration)
                        ,
                        formSent = formSent3,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        )
                    )

                    FormInput(
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (groups != null) {
                            val newGroups =
                                groups + RestaurantGroupDTO(name = restaurantViewModel.name.value)

                            if (!addGroup) {
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    val expandedState = remember { mutableStateOf(false) }

                                    ComboBox(
                                        expanded = expandedState,
                                        value = selectedGroup?.name ?: "",
                                        onValueChange = { name ->
                                            val tmp = newGroups.find { it.name == name }
                                            restaurantViewModel.selectedToGroup.value = tmp
                                            restaurantViewModel.selectedGroup = tmp

                                        },
                                        options = newGroups.map { it.name },
                                        label = stringResource(R.string.label_add_to_group),
                                        isError = restaurantViewModel.isGroupInvalid(),
                                        errorText = stringResource(
                                            if (restaurantViewModel.getGroupError() != -1 && formSent3)
                                                restaurantViewModel.getGroupError()
                                            else
                                                R.string.error_registerRestaurant_invalid_group
                                        )
                                    )

                                }

                                SecondaryButton(
                                    onClick = { addGroup = true },
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Group",
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .align(Alignment.CenterVertically)
                                        .fillMaxWidth(0.2f)
                                )

                            } else {
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    FormInput(
                                        inputText = restaurantViewModel.newGroup.value,
                                        onValueChange = {
                                            restaurantViewModel.newGroup.value = it
                                        },
                                        label = stringResource(id = R.string.label_new_group),
                                        isError = restaurantViewModel.isGroupInvalid(),
                                        errorText = stringResource(
                                            if (restaurantViewModel.getGroupError() != -1)
                                                restaurantViewModel.getGroupError()
                                            else
                                                R.string.error_registerRestaurant_invalid_description
                                        ),
                                        formSent = formSent3,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                SecondaryButton(
                                    onClick = { addGroup = false },
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Remove Group",
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .align(Alignment.CenterVertically)
                                        .fillMaxWidth(0.2f)
                                )
                            }
                        }
                    }

                }

                Spacer(modifier = Modifier.height(32.dp))


                ShowErrorToast(
                    context = LocalContext.current,
                    id = restaurantViewModel.getToastError3()
                )
                var successText = ""
                val errorText = stringResource(R.string.error_create_restaurant)

                if(restaurantId == null && group == null)
                    successText = stringResource(R.string.label_restaurant_register_complete)
                else
                    successText = stringResource(R.string.label_restaurant_saved)

                ButtonComponent(
                    label = if (restaurantId == null && group == null)
                        stringResource(id = R.string.label_register_restaurant)
                    else
                        stringResource(id = R.string.label_save),
                    isLoading = isLoading,
                    onClick = {
                        restaurantViewModel.viewModelScope.launch {
                            isLoading = true
                            formSent3 = true
                            val success = if (restaurantId == null && group == null) {
                                restaurantViewModel.registerRestaurant(context)
                            } else {
                                restaurantViewModel.editRestaurant(context)
                            }

                            if (success) {
                                navControllerHome.navigate(MainRoutes.Home)
                                Toast.makeText(
                                    context,
                                    successText,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }else{
                                Toast.makeText(
                                    context,
                                    errorText,
                                    Toast.LENGTH_SHORT
                                ).show()
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
