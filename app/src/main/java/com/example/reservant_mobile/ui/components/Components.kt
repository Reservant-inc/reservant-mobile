package com.example.reservant_mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.viewmodels.Calendar
import com.example.reservant_mobile.ui.viewmodels.RegisterViewModel
import java.time.LocalDate
import java.time.YearMonth

val roundedShape = RoundedCornerShape(12.dp)

@Composable
fun InputUserInfo(
    modifier: Modifier = Modifier,
    inputText: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    shape: RoundedCornerShape = roundedShape,
    errorText: String = "",
    showError: Boolean = true

) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        value = inputText,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        shape = shape,
        isError = isError
    )
    if (isError && showError) {
        Text(
            text = errorText,
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}


@Composable
fun UserButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String = "",
) {
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
fun Logo() {
    Image(
        painter = painterResource(id = R.drawable.ic_logo),
        contentDescription = "Logo",
        modifier = Modifier.size(120.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    modifier: Modifier = Modifier,
    label: String,
    itemsList: List<String>,
    shape: RoundedCornerShape = roundedShape,
    onItemSelected: (String) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }

    Box(
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                if (enabled) {
                    expanded = it
                }
            }
        ) {
            TextField(
                label = { Text(text = label) },
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(),
                shape = shape,
                enabled = enabled,
                isError = isError
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    if (enabled) {
                        expanded = false
                    }
                }
            ) {
                itemsList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            if (enabled) {
                                selectedText = item
                                expanded = false
                                onItemSelected(item)
                            }
                        },
                        enabled = enabled
                    )
                }
            }
        }
    }
}


@Composable
fun BirthdayInput(
    calendar: Calendar,
    onYearChange: (String) -> Unit,
    onMonthChange: (String) -> Unit,
    onDayChange: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        DropdownMenuBox(
            label = "Year",
            itemsList = (1900..LocalDate.now().year - 18).map { it.toString() }.reversed(),
            modifier = Modifier.weight(1f),
            onItemSelected = { value ->
                onYearChange(value)
            }
        )

        DropdownMenuBox(
            label = "Month",
            itemsList = (1..12).map { it.toString() },
            modifier = Modifier.weight(1f),
            onItemSelected = { value ->
                if (calendar.monthOfBirth.length == 1) {
                    onMonthChange("0$value")
                } else {
                    onMonthChange(value)
                }
            },
            enabled = calendar.yearOfBirth.isNotEmpty(),
        )

        DropdownMenuBox(
            label = "Day",
            itemsList = calendar.getDaysList(calendar.yearOfBirth, calendar.monthOfBirth),
            modifier = Modifier.weight(1f),
            onItemSelected = { value ->
                if (calendar.dayOfBirth.length == 1) {
                    onDayChange("0$value")
                } else {
                    onDayChange(value)
                }
            },
            enabled = calendar.monthOfBirth.isNotEmpty(),
        )
    }
}


@Composable
fun PhoneInput(
    registerViewModel: RegisterViewModel,
    isError: Boolean = false,
    errorText: String = ""
) {
    Row(Modifier.fillMaxWidth()) {
        DropdownMenuBox(
            label = "Prefix",
            itemsList = registerViewModel.getCountryCodesWithPrefixes(),
            modifier = Modifier.weight(0.33f),
            onItemSelected = { value ->
                registerViewModel.prefix = "00" + value.substringAfter(" - ").trim()
            },
            isError = isError
        )
        Spacer(modifier = Modifier.weight(0.01f))
        InputUserInfo(
            modifier = Modifier.weight(0.66f),
            inputText = registerViewModel.number,
            onValueChange = { registerViewModel.number = it },
            label = "Phone number",
            isError = isError,
            showError = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        )
    }
    if (isError) {
        Text(
            text = errorText,
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    //preview if needed
}
