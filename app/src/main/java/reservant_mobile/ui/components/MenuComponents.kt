package reservant_mobile.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.RestaurantMenuDTO
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

@Composable
fun MenuCard(
    showConfirmDeletePopup: MutableState<Boolean> = mutableStateOf(false),
    showEditPopup: MutableState<Boolean> = mutableStateOf(false),
    name: FormField,
    altName: FormField,
    menuType: FormField,
    menuTypes: List<String>,
    dateFrom: FormField,
    dateUntil: FormField,
    photo: ImageBitmap? = null,
    getPhoto: suspend () -> Bitmap?,
    menu: RestaurantMenuDTO,
    onFilePicked: (Uri?) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    clearFields: () -> Unit,
    onClick: () -> Unit,
    isFetching: Boolean = false,
    isSaving: Boolean = false,
    fileTooLarge: Int = -1,
    fileErrors: Int = -1,
    isNameInvalid: Boolean = false,
    isAltNameInvalid: Boolean = false,
    isMenuTypeInvalid: Boolean = false
) {

    when {
        showConfirmDeletePopup.value -> {
            DeleteCountdownPopup(
                icon = Icons.Filled.DeleteForever,
                title = stringResource(id = R.string.confirm_delete_title),
                text = stringResource(id = R.string.confirm_delete_text),
                onConfirm = {
                    onDeleteClick()
                },
                onDismissRequest = { showConfirmDeletePopup.value = false },
                confirmText = stringResource(id = R.string.label_yes_capital),
                dismissText = stringResource(id = R.string.label_cancel),
                isSaving = isSaving
            )
        }

        showEditPopup.value -> {
            name.value = menu.name
            altName.value = menu.alternateName ?: ""
            menuType.value = menu.menuType
            dateFrom.value = menu.dateFrom
            dateUntil.value = menu.dateUntil ?: ""

            MenuPopup(
                title = { Text(text = stringResource(id = R.string.label_edit_menu)) },
                hide = { showEditPopup.value = false },
                onConfirm = onEditClick,
                clear = clearFields,
                onFilePicked = onFilePicked,
                name = name,
                altName = altName,
                menuType = menuType,
                menuTypes = menuTypes,
                dateFrom = dateFrom,
                dateUntil = dateUntil,
                isSaving = isSaving,
                fileTooLarge = fileTooLarge,
                fileErrors = fileErrors,
                isNameInvalid = isNameInvalid,
                isAltNameInvalid = isAltNameInvalid,
                isMenuTypeInvalid = isMenuTypeInvalid
            )
        }
    }


    val loadingModifier = when {
        isFetching -> Modifier
            .shimmer()
            .alpha(0F)
        else -> Modifier
    }

    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
            .then(loadingModifier)
    ) {
        Column {
            /*if (photo != null) {
                Image(
                    bitmap = photo,
                    contentDescription = "${menu.name}_photo",
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Image(
                    painterResource(id = R.drawable.unknown_image),
                    contentDescription = "placeholder_photo",
                    modifier = Modifier
                        .size(140.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
            }*/
            LoadedPhotoComponent (
                photoModifier = Modifier.fillMaxWidth(),
                placeholderModifier = Modifier
                    .size(140.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                getPhoto = getPhoto
            )



            Box(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                ) {

                    val namePadding = when {
                        menu.alternateName == null -> 8.dp
                        else -> 2.dp
                    }

                    Text(
                        text = menu.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp, bottom = namePadding, top = 8.dp)
                    )

                    menu.alternateName?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        )
                    }

                    menu.dateUntil?.let {
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.label_limited_time))
                                append(": ")
                                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                                append(it)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ) {
                    val buttonModifier = Modifier
                        .align(Alignment.Bottom)
                        .size(50.dp)
                        .padding(6.dp)

                    SecondaryButton(
                        modifier = buttonModifier,
                        onClick = { showEditPopup.value = true },
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "EditMenuItem"
                    )

                    SecondaryButton(
                        modifier = buttonModifier,
                        onClick = { showConfirmDeletePopup.value = true },
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = "delete"
                    )
                }

                /*Text(
                    text = menu.menuType,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(8.dp)
                )*/

            }
        }
    }
}

@Composable
fun AddMenuButton(
    name: FormField,
    altName: FormField,
    menuType: FormField,
    menuTypes: List<String>,
    dateFrom: FormField,
    dateUntil: FormField,
    onFilePicked: (Uri?) -> Unit,
    clearFields: () -> Unit,
    addMenu: () -> Unit,
    isSaving: Boolean = false,
    fileTooLarge: Int = -1,
    fileErrors: Int = -1,
    isNameInvalid: Boolean = false,
    isAltNameInvalid: Boolean = false,
    isMenuTypeInvalid: Boolean = false,
    showAddDialog: MutableState<Boolean> = mutableStateOf(false)
) {
    when {
        showAddDialog.value -> {
            MenuPopup(
                title = { Text(text = stringResource(id = R.string.label_add_menu)) },
                hide = { showAddDialog.value = false },
                onConfirm = addMenu,
                clear = clearFields,
                onFilePicked = onFilePicked,
                fileTooLarge = fileTooLarge,
                fileErrors = fileErrors,
                name = name,
                altName = altName,
                menuType = menuType,
                menuTypes = menuTypes,
                dateFrom = dateFrom,
                dateUntil = dateUntil,
                isSaving = isSaving,
                isNameInvalid = isNameInvalid,
                isAltNameInvalid = isAltNameInvalid,
                isMenuTypeInvalid = isMenuTypeInvalid
            )
        }
    }

    MyFloatingActionButton(
        onClick = { showAddDialog.value = true }
    )
}

@Composable
fun MenuTypeButton(
    modifier: Modifier = Modifier,
    menuType: String,
    onMenuClick: () -> Unit
) {
    Button(
        onClick = { onMenuClick() },
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = modifier.padding(4.dp)
    ) {
        Text(menuType)
    }
}