package reservant_mobile.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import reservant_mobile.data.utils.getCountryDetailsByCode
import reservant_mobile.ui.viewmodels.EmployeeViewModel

@Composable
fun EmployeeCard(
    employee: RestaurantEmployeeDTO,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showConfirmDeletePopup by remember { mutableStateOf(false) }

    when {
        showConfirmDeletePopup -> {
            DeleteCountdownPopup(
                icon = Icons.Filled.DeleteForever,
                title = stringResource(id = R.string.confirm_delete_title),
                text = stringResource(id = R.string.confirm_delete_text),
                onConfirm = {
                    onDeleteClick()
                    showConfirmDeletePopup = false
                },
                onDismissRequest = { showConfirmDeletePopup = false },
                confirmText = stringResource(id = R.string.label_yes_capital),
                dismissText = stringResource(id = R.string.label_cancel)
            )
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        val localDensity = LocalDensity.current
        var tabWidth by remember { mutableStateOf(0.dp) }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopStart
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${employee.firstName} ${employee.lastName}",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                    )

                    Text(
                        text = "${stringResource(id = R.string.label_login_display)} ${employee.login}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = "${stringResource(id = R.string.label_phone_display)} ${employee.phoneNumber}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                tabWidth = with(localDensity) { coordinates.size.width.toDp() }
                            }
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .width(tabWidth),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )


                    if (employee.isHallEmployee) {
                        Text(
                            text = stringResource(id = R.string.label_employee_hall),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (employee.isBackdoorEmployee) {
                        Text(
                            text = stringResource(id = R.string.label_employee_backdoor),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Row {
                val buttonModifier = Modifier
                    .size(50.dp)
                    .padding(6.dp)

                SecondaryButton(
                    modifier = buttonModifier,
                    onClick = onEditClick,
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "EditEmployee"
                )

                SecondaryButton(
                    modifier = buttonModifier,
                    onClick = { showConfirmDeletePopup = true },
                    imageVector = Icons.Filled.DeleteForever,
                    contentDescription = "DeleteEmployee"
                )
            }
        }

    }
}

@Composable
fun AddEmployeeDialog(onDismiss: () -> Unit, vm: EmployeeViewModel) {
    vm.clearFields()
    var formSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.label_employee_add)) },
        text = {
            Column {
                FormInput(
                    inputText = vm.login.value,
                    onValueChange = { vm.login.value = it },
                    label = stringResource(id = R.string.label_login),
                    optional = false,
                    isError = vm.isLoginInvalid(),
                    errorText = stringResource(
                        if (vm.getLoginError() != -1)
                            vm.getLoginError()
                        else
                            R.string.error_login_invalid
                    ),
                    formSent = formSent
                )
                FormInput(
                    inputText = vm.firstName.value,
                    onValueChange = { vm.firstName.value = it },
                    label = stringResource(id = R.string.label_name),
                    optional = false,
                    isError = vm.isFirstNameInvalid(),
                    errorText = stringResource(
                        if (vm.getFirstNameError() != -1)
                            vm.getFirstNameError()
                        else
                            R.string.error_register_invalid_name
                    ),
                    formSent = formSent
                )
                FormInput(
                    inputText = vm.lastName.value,
                    onValueChange = { vm.lastName.value = it },
                    label = stringResource(id = R.string.label_lastname),
                    optional = false,
                    isError = vm.isLastNameInvalid(),
                    errorText = stringResource(
                        if (vm.getLastNameError() != -1)
                            vm.getLastNameError()
                        else
                            R.string.error_register_invalid_lastname
                    ),
                    formSent = formSent
                )

                MyDatePickerDialog(onDateChange = { birthday -> vm.birthday.value = birthday })

                FormInput(
                    inputText = vm.phoneNum.value,
                    onValueChange = { vm.phoneNum.value = it },
                    label = stringResource(id = R.string.label_phone),
                    optional = false,
                    leadingIcon = {
                        CountryPickerView(
                            countries = vm.countriesList,
                            selectedCountry = getCountryDetailsByCode(vm.mobileCountry.value)!!,
                            onSelection = { selectedCountry ->
                                vm.mobileCountry.value = selectedCountry.code
                            },
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    isError = vm.isPhoneInvalid(),
                    errorText = stringResource(
                        if (vm.getPhoneError() != -1)
                            vm.getPhoneError()
                        else
                            R.string.error_register_invalid_phone
                    ),
                    formSent = formSent
                )

                FormInput(
                    inputText = vm.password.value,
                    onValueChange = { vm.password.value = it },
                    label = stringResource(id = R.string.label_password),
                    leadingIcon = {
                        androidx.compose.material3.IconButton(onClick = {
                            isPasswordVisible = !isPasswordVisible
                        }) {
                            Icon(
                                imageVector = if (isPasswordVisible)
                                    Icons.Filled.Visibility
                                else
                                    Icons.Filled.VisibilityOff,
                                contentDescription = stringResource(R.string.label_password_visibility)
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    isError = vm.isPasswordInvalid(),
                    errorText = stringResource(
                        if (vm.getPasswordError() != -1)
                            vm.getPasswordError()
                        else
                            R.string.error_register_invalid_password
                    ),
                    formSent = formSent
                )


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = vm.isHallEmployee,
                        onCheckedChange = { isChecked ->
                            vm.isHallEmployee = isChecked
                        }
                    )
                    Text(stringResource(id = R.string.label_employee_hall))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = vm.isBackdoorEmployee,
                        onCheckedChange = { isChecked ->
                            vm.isBackdoorEmployee = isChecked
                        }
                    )
                    Text(stringResource(id = R.string.label_employee_backdoor))
                }
            }
        },
        confirmButton = {
            ShowErrorToast(context = LocalContext.current, id = vm.getToastError())
            ButtonComponent(
                onClick = {
                    vm.viewModelScope.launch {
                        isLoading = true
                        formSent = true

                        if (vm.register()) {
                            onDismiss()
                        }

                        isLoading = false
                    }
                },
                label = stringResource(R.string.label_signup)
            )
        },
        dismissButton = {
            ButtonComponent(onClick = onDismiss, label = stringResource(id = R.string.label_cancel))
        }
    )
}

@Composable
fun EditEmployeeDialog(
    employee: RestaurantEmployeeDTO,
    onDismiss: () -> Unit,
    vm: EmployeeViewModel
) {
    vm.firstName.value = employee.firstName.toString()
    vm.lastName.value = employee.lastName.toString()
    vm.birthday.value = employee.birthDate.toString()
    vm.phoneNum.value = employee.phoneNumber!!.number
    vm.mobileCountry.value = employee.phoneNumber.code.replace("+", "")
    vm.isHallEmployee = employee.isHallEmployee
    vm.isBackdoorEmployee = employee.isBackdoorEmployee

    var formSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.label_employee_edit)) },
        text = {
            Column {
                FormInput(
                    inputText = vm.firstName.value,
                    onValueChange = { vm.firstName.value = it },
                    label = stringResource(id = R.string.label_name),
                    optional = false,
                    isError = vm.isFirstNameInvalid(),
                    errorText = stringResource(
                        if (vm.getFirstNameError() != -1)
                            vm.getFirstNameError()
                        else
                            R.string.error_register_invalid_name
                    ),
                    formSent = formSent
                )
                FormInput(
                    inputText = vm.lastName.value,
                    onValueChange = { vm.lastName.value = it },
                    label = stringResource(id = R.string.label_lastname),
                    optional = false,
                    isError = vm.isLastNameInvalid(),
                    errorText = stringResource(
                        if (vm.getLastNameError() != -1)
                            vm.getLastNameError()
                        else
                            R.string.error_register_invalid_lastname
                    ),
                    formSent = formSent
                )
                MyDatePickerDialog(
                    startStringValue = vm.birthday.value,
                    onDateChange = { birthday -> vm.birthday.value = birthday }
                )

                FormInput(
                    inputText = vm.phoneNum.value,
                    onValueChange = { vm.phoneNum.value = it },
                    label = stringResource(id = R.string.label_phone),
                    leadingIcon = {
                        CountryPickerView(
                            countries = vm.countriesList,
                            selectedCountry = getCountryDetailsByCode(vm.mobileCountry.value)!!,
                            onSelection = { selectedCountry ->
                                vm.mobileCountry.value = selectedCountry.code
                            },
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    optional = false,
                    isError = vm.isPhoneInvalid(),
                    errorText = stringResource(
                        if (vm.getPhoneError() != -1)
                            vm.getPhoneError()
                        else
                            R.string.error_register_invalid_phone
                    ),
                    formSent = formSent
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = vm.isHallEmployee,
                        onCheckedChange = { isChecked ->
                            vm.isHallEmployee = isChecked
                        }
                    )
                    Text(stringResource(id = R.string.label_employee_hall))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = vm.isBackdoorEmployee,
                        onCheckedChange = { isChecked ->
                            vm.isBackdoorEmployee = isChecked
                        }
                    )
                    Text(stringResource(id = R.string.label_employee_backdoor))
                }
            }
        },
        confirmButton = {
            ButtonComponent(
                onClick = {
                    vm.viewModelScope.launch {
                        isLoading = true
                        formSent = true

                        if (vm.editEmployee(employee)) {
                            onDismiss()
                        }

                        isLoading = false
                    }
                },
                label = stringResource(R.string.label_save)
            )
        },
        dismissButton = {
            ButtonComponent(onClick = onDismiss, label = stringResource(id = R.string.label_cancel))
        }
    )
}