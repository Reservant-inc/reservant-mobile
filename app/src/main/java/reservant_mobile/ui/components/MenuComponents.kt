package reservant_mobile.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.fields.FormField
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MenuPopup(
    title: @Composable (() -> Unit),
    hide: () -> Unit,
    onConfirm: () -> Unit,
    clear: () -> Unit,
    onFilePicked: (Uri?) -> Unit,
    fileTooLarge: Int = -1,
    fileErrors: Int = -1,
    name: FormField,
    isNameInvalid: Boolean = false,
    altName: FormField,
    isAltNameInvalid: Boolean = false,
    menuType: FormField,
    menuTypes: List<String>,
    isMenuTypeInvalid: Boolean = false,
    dateFrom: FormField,
    dateUntil: FormField,
    isSaving: Boolean = false
) {

    val expanded = remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = {
            hide()
            clear()
        },
        title = title,
        text = {
            Column {
                FormInput(
                    label = stringResource(id = R.string.label_restaurant_name),
                    inputText = name.value,
                    onValueChange = { name.value = it },
                    isError = isNameInvalid,
                    errorText = stringResource(id = R.string.error_invalid_menu_name)
                )
                FormInput(
                    label = stringResource(id = R.string.label_alternate_name),
                    optional = true,
                    inputText = altName.value,
                    onValueChange = { altName.value = it },
                    isError = isAltNameInvalid,
                    errorText = stringResource(id = R.string.error_invalid_menu_name)
                )
                ComboBox(
                    label = stringResource(id = R.string.label_menu_type),
                    value = menuType.value,
                    onValueChange = { menuType.value = it },
                    expanded = expanded,
                    options = menuTypes,
                    isError = isMenuTypeInvalid,
                    errorText = stringResource(id = R.string.error_invalid_menu_type)
                )
                MyDatePickerDialog(
                    label = { Text(text = stringResource(id = R.string.label_date_from)) },
                    allowFutureDates = true,
                    startStringValue = dateFrom.value,
                    startDate = dateFrom.value.ifEmpty {
                        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                    },
                    onDateChange = { dateFrom.value = it }
                )
                MyDatePickerDialog(
                    label = {
                        Text(text = buildAnnotatedString {
                            append(stringResource(id = R.string.label_date_to))
                            pushStyle(
                                SpanStyle(
                                    Color.Gray,
                                    fontWeight = FontWeight.Light,
                                    fontStyle = FontStyle.Italic
                                )
                            )
                            append(stringResource(id = R.string.label_optional))
                        })
                    },
                    allowFutureDates = true,
                    startStringValue = dateUntil.value,
                    startDate = dateUntil.value.ifEmpty {
                        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                    },
                    onDateChange = { dateUntil.value = it }
                )
                FormFileInput(
                    label = stringResource(id = R.string.label_menu_photo),
                    onFilePicked = onFilePicked,
                    context = LocalContext.current,
                    isError = fileErrors != -1 || fileTooLarge != -1,
                    errorText = if (fileTooLarge != -1) stringResource(id = fileTooLarge, 1024)
                    else if (fileErrors != -1) stringResource(id = fileErrors)
                    else ""
                )

            }
        },
        dismissButton = {
            ButtonComponent(
                onClick = {
                    hide()
                    clear()
                },
                label = stringResource(id = R.string.label_cancel)
            )
        },
        confirmButton = {
            ButtonComponent(
                onClick = {
                    onConfirm()
                },
                label = stringResource(id = R.string.label_save),
                isLoading = isSaving
            )
        },

        )
}