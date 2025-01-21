package reservant_mobile.ui.components

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.reservant_mobile.R
import reservant_mobile.data.utils.Country
import reservant_mobile.data.utils.getFileName
import reservant_mobile.data.utils.getFlagEmojiFor
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun FormInput(
    modifier: Modifier = Modifier,
    inputText: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    errorText: String = "",
    formSent: Boolean = false,
    optional: Boolean = false,
    maxLines: Int = 1,
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    isDisabled: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    readOnly: Boolean = false
) {

    var beginValidation: Boolean by remember {
        mutableStateOf(false)
    }

    if (inputText.isNotEmpty())
        beginValidation = true

    if (inputText.isEmpty() && optional)
        beginValidation = false

    // If optional && the field is blank => override isError to false
    val finalIsError = if (optional && inputText.isBlank()) false else isError

    Column {
        OutlinedTextField(
            modifier =
            if (optional) {
                modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            } else {
                modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .onFocusChanged {
                        if (it.hasFocus) beginValidation = true
                    }
            },
            value = inputText,
            onValueChange = onValueChange,
            label = {
                Row {
                    Text(text = label)
                    if (optional)
                        Text(
                            text = stringResource(id = R.string.label_optional),
                            color = MaterialTheme.colorScheme.outline,
                            fontStyle = FontStyle.Italic
                        )

                }
            },
            placeholder = { Text(text = placeholder) },
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions.copy(
                imeAction = if (keyboardOptions.imeAction == ImeAction.Default)
                    ImeAction.Next
                else keyboardOptions.imeAction
            ),
            shape = shape,
            isError = isError && (beginValidation || formSent),
            maxLines = maxLines,
            singleLine = maxLines == 1,
            leadingIcon = leadingIcon,
            enabled = !isDisabled,
            interactionSource = interactionSource,
            readOnly = readOnly
        )
        if (finalIsError && (beginValidation || formSent)) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTimePickerDialog(
    initialTime: String = "",
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onlyHalfHours: Boolean = false,
    minTime: String? = null,
    isError: Boolean = false,
    errorText: String? = null
) {
    val context = LocalContext.current
    val currentTime = Calendar.getInstance()

    val initialHour = if (initialTime.isNotEmpty()) initialTime.substringBefore(":").toInt() else currentTime.get(Calendar.HOUR_OF_DAY)
    val initialMinute = if (initialTime.isNotEmpty()) initialTime.substringAfter(":").toInt() else currentTime.get(Calendar.MINUTE)

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    var showDialog by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf(String.format("%02d:%02d", initialHour, initialMinute)) }

    LaunchedEffect(Unit) {
        onTimeSelected(selectedTime)
    }

    fun parseTimeToMinutes(timeString: String): Int {
        if (timeString.isBlank() || !timeString.contains(":")) {
            // Możesz zwrócić -1, 0, lub inną wartość oznaczającą "błąd/wybierz czas"
            return -1
        }

        val parts = timeString.split(":")
        if (parts.size != 2) {
            return -1
        }
        val hour = parts[0].toIntOrNull() ?: return -1
        val minute = parts[1].toIntOrNull() ?: return -1

        return hour * 60 + minute
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    Column(modifier = modifier) {
        OutlinedButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
            enabled = enabled
        ) {
            Text(
                text = selectedTime,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        var hour = timePickerState.hour
                        var minute = timePickerState.minute

                        // Logika half hours
                        // Jeśli minTime != null, zawsze używamy half hours
                        // Jeśli minTime == null, używamy half hours tylko jeśli onlyHalfHours = true
                        val enforceHalfHours = minTime != null || onlyHalfHours

                        if (enforceHalfHours) {
                            when {
                                minute == 0 || minute == 30 -> {
                                }
                                minute < 30 -> {
                                    minute = 30
                                }
                                else -> {
                                    minute = 0
                                    hour = (hour + 1) % 24
                                }
                            }
                        }

                        val chosenTime = String.format("%02d:%02d", hour, minute)

                        // Jeśli minTime != null, sprawdzamy czy chosenTime > minTime
                        if (minTime != null) {
                            val chosenMinutes = parseTimeToMinutes(chosenTime)
                            val minMinutes = parseTimeToMinutes(minTime)
                            if (chosenMinutes <= minMinutes) {
                                // Musi być ściśle większy
                                showToast(context.getString(R.string.time_too_early))
                                return@TextButton
                            }
                        }

                        selectedTime = chosenTime
                        onTimeSelected(selectedTime)
                        showDialog = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.label_cancel))
                    }
                },
                text = {
                    TimeInput(state = timePickerState)
                }
            )
        }
        if (isError) {
            Text(
                text = errorText ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}


@Composable
fun FormFileInput(
    label: String = "",
    defaultValue: String = "",
    onFilePicked: (Uri?) -> Unit,
    modifier: Modifier = Modifier,
    context: Context,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    isError: Boolean = false,
    errorText: String = "",
    formSent: Boolean = false,
    optional: Boolean = false,
    deletable: Boolean = false
) {
    var fileName by remember { mutableStateOf<String?>(null) }
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fileName = uri?.let { getFileName(context, it.toString()) }
        onFilePicked(uri)
    }
    var beginValidation: Boolean by remember { mutableStateOf(false) }

    if (fileName != null) {
        beginValidation = true
    }
    if (fileName == null && optional) {
        beginValidation = false
    }

    if (fileName == null && defaultValue.isNotBlank()) {
        fileName = defaultValue
    }

    OutlinedTextField(
        modifier =
        if (optional) {
            modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        } else {
            modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .onFocusChanged {
                    if (it.hasFocus) {
                        beginValidation = true
                    }
                }
        },
        value = fileName ?: "",
        onValueChange = { },
        label = {
            Row {
                Text(text = label)
                if (optional) {
                    Text(
                        text = stringResource(R.string.label_optional),
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        },
        readOnly = true,
        visualTransformation = VisualTransformation.None,
        keyboardOptions = KeyboardOptions.Default,
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            pickFileLauncher.launch("*/*")
                        }
                    }
                }
            },
        trailingIcon = {
            if (deletable && fileName != null) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete file",
                    modifier = Modifier.clickable {
                        fileName = null
                        onFilePicked(null)
                    }
                )
            } else if (fileName == null) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach file"
                )
            }
        },
        shape = shape,
        isError = isError && (beginValidation || formSent),
    )

    if (isError && (beginValidation || formSent)) {
        Text(
            text = errorText,
            color = Color.Red
        )
    }
}

@Composable
fun MyDatePickerDialog(
    modifier: Modifier = Modifier,
    onDateChange: (String) -> Unit,
    label: String = stringResource(R.string.label_register_birthday_select),
    startStringValue: String = stringResource(id = R.string.label_register_birthday_dialog),
    allowFutureDates: Boolean = false,
    allowPastDates: Boolean = true,
    startDate: String = (LocalDate.now().year - 28).toString() + "-06-15",
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    isError: Boolean = false,
    errorText: String? = null,
    optional: Boolean = false
) {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DatePickerDialog(
        onDateSelected: (String) -> Unit,
        onDismiss: () -> Unit,
        allowFutureDates: Boolean,
        allowPastDates: Boolean,
        startDate: String
    ) {
        fun convertMillisToDate(millis: Long): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return try {
                formatter.format(Date(millis))
            } catch (_: Exception) {
                ""
            }
        }

        fun convertDateToMillis(date: String): Long {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return try {
                formatter.parse(date)?.time ?: 0L
            } catch (_: Exception) {
                0L
            }
        }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = convertDateToMillis(startDate),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val today = System.currentTimeMillis()
                    val startOfToday = LocalDate.now()
                        .atStartOfDay()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    return when {
                        !allowFutureDates && utcTimeMillis > today -> false
                        !allowPastDates && utcTimeMillis < startOfToday -> false
                        else -> true
                    }
                }
            }
        )

        val selectedDate = datePickerState.selectedDateMillis?.let {
            convertMillisToDate(it)
        } ?: ""

        androidx.compose.material3.DatePickerDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                Button(onClick = {
                    onDateSelected(selectedDate)
                    onDismiss()
                }) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    var date by remember { mutableStateOf(startStringValue) }
    var showDatePicker by remember { mutableStateOf(false) }

    FormInput(
        inputText = date,
        onValueChange = { },
        label = label,
        readOnly = true,
        shape = shape,
        optional = optional,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            showDatePicker = true
                        }
                    }
                }
            },
        isError = isError,
        errorText = errorText?:""
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = {
                date = it
                onDateChange(it)
            },
            onDismiss = { showDatePicker = false },
            allowFutureDates = allowFutureDates,
            allowPastDates = allowPastDates,
            startDate = startDate
        )
    }
}

@Composable
fun CountryPickerView(
    selectedCountry: Country,
    onSelection: (Country) -> Unit,
    countries: List<Country>
) {
    var showDialog by remember { mutableStateOf(false) }
    Text(
        modifier = Modifier
            .clickable {
                showDialog = true
            }
            .padding(start = 20.dp, end = 5.dp)
            .testTag("CountryPicker"),
        text = "${getFlagEmojiFor(selectedCountry.nameCode)} +${selectedCountry.code}"
    )

    if (showDialog)
        CountryCodePickerDialog(countries, onSelection) {
            showDialog = false
        }
}

@Composable
fun CountryCodePickerDialog(
    countries: List<Country>,
    onSelection: (Country) -> Unit,
    dismiss: () -> Unit,
) {
    Dialog(onDismissRequest = dismiss) {
        Box {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 40.dp)
                    .background(shape = RoundedCornerShape(20.dp), color = Color.White)
            ) {
                for (country in countries) {
                    item {
                        Text(
                            modifier = Modifier
                                .clickable {
                                    onSelection(country)
                                    dismiss()
                                }
                                .fillMaxWidth()
                                .padding(10.dp),
                            text = "${getFlagEmojiFor(country.nameCode)} ${country.fullName}"
                        )
                    }
                }
            }
        }
    }
}