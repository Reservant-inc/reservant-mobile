package com.example.reservant_mobile.ui.components

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import com.example.reservant_mobile.data.utils.BottomNavItem
import com.example.reservant_mobile.data.utils.Country
import com.example.reservant_mobile.data.utils.getFileName
import com.example.reservant_mobile.data.utils.getFlagEmojiFor
import com.example.reservant_mobile.ui.theme.AppTheme
import kotlinx.coroutines.delay
import com.example.reservant_mobile.ui.viewmodels.EmployeeViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

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
    errorText: String = "",
    formSent: Boolean = false,
    optional: Boolean = false,
    maxLines: Int = 1,
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
) {

    var beginValidation: Boolean by remember {
        mutableStateOf(false)
    }

    if (inputText.isNotEmpty())
        beginValidation = true

    if (inputText.isEmpty() && optional)
        beginValidation = false

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
            leadingIcon = leadingIcon,

            )
        if (isError && (beginValidation || formSent)) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

}

@Composable
fun InputUserFile(
    label: String = "",
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
        fileName = uri?.let { getFileName(context, it) }
        onFilePicked(uri)
    }
    var beginValidation: Boolean by remember { mutableStateOf(false) }

    if (fileName != null) {
        beginValidation = true
    }
    if (fileName == null && optional) {
        beginValidation = false
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
fun OutLinedDropdownMenu(
    selectedOption: String,
    itemsList: List<String>,
    onOptionSelected: (String) -> Unit,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String = "",
    formSent: Boolean = false,
    label: String = "",
    optional: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    var beginValidation: Boolean by remember {
        mutableStateOf(false)
    }

    if (selectedOption.isNotEmpty())
        beginValidation = true
    if (selectedOption.isEmpty() && optional)
        beginValidation = false

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
            value = selectedOption,
            onValueChange = { },
            readOnly = true,
            label = {
                Row {
                    Text(text = label)
                    if (optional)
                        Text(
                            text = stringResource(id = R.string.label_optional),
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic
                        )
                }
            },
            interactionSource = interactionSource,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Hide" else "Show"
                )
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

        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Release) {
                    expanded = true
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsList.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ButtonComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String = "",
    isLoading: Boolean = false
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(gradientBrush, RoundedCornerShape(16.dp)),  // Gradient tła
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,  // Transparentny, by pokazać gradient
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(text = label)
        }
    }
}


@Composable
fun TagsSelection(
    tags: List<String>,
    selectedTags: List<String>,
    onTagSelected: (String, Boolean) -> Unit,
) {
    Column {
        tags.forEach { tag ->
            val isChecked = selectedTags.contains(tag)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isSelected ->
                        onTagSelected(tag, isSelected)
                    }
                )
                Text(
                    text = tag,
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .clickable { onTagSelected(tag, !isChecked) }
                )
            }
        }
    }
}

@Composable

fun Logo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_logo),
        contentDescription = "Logo",
        modifier = modifier.size(120.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun convertDateToMillis(date: String): Long {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.parse(date)?.time ?: 0L
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = convertDateToMillis((LocalDate.now().year - 28).toString() + "-06-15"),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(
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


@Composable
fun MyDatePickerDialog(onBirthdayChange: (String) -> Unit) {
    // TODO: insert resource stringResource(R.string.label_register_birthday_dialog)
    var date by remember { mutableStateOf("Open date picker dialog") }
    var showDatePicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = date,
        onValueChange = { },
        label = { Text(stringResource(R.string.label_register_birthday_select)) },
        readOnly = true,
        shape = roundedShape,
        modifier = Modifier
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
            }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = {
                date = it
                onBirthdayChange(it)
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun RestaurantInfoView(
    restaurant: RestaurantDTO,
    onEditClick: () -> Unit,
    onManageEmployeeClick: () -> Unit,
    onManageMenuClick: () -> Unit,
    onManageSubscriptionClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_logo),
                        contentDescription = "Restaurant Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${restaurant.name} - ${restaurant.restaurantType}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                DetailItem(
                    label = stringResource(R.string.label_restaurant_nip),
                    value = restaurant.nip
                )
                DetailItem(
                    label = stringResource(R.string.label_restaurant_address),
                    value = "${restaurant.address}, ${restaurant.postalIndex}"
                )
                DetailItem(
                    label = stringResource(R.string.label_restaurant_city),
                    value = restaurant.city
                )
                DetailItem(
                    label = stringResource(R.string.label_restaurant_delivery),
                    value =
                    if (restaurant.provideDelivery)
                        stringResource(R.string.label_restaurant_delivery_available)
                    else
                        stringResource(R.string.label_restaurant_delivery_not_available)
                )
                DetailItem(
                    label = stringResource(R.string.label_restaurant_description),
                    value = restaurant.description
                )
                if (restaurant.tags.isNotEmpty()) {
                    TagsDetailView(tags = restaurant.tags)
                }
                DetailItem(
                    label = stringResource(R.string.label_restaurant_tables),
                    value = "${restaurant.tables.size}"
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopEnd)
                    .offset(x = 16.dp)
                    .offset(y = (-12).dp)
            ) {
                IconButton(
                    onClick = { showMenu = !showMenu },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More Options")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ) {
                    DropdownMenuItem(
                        onClick = {
                            onEditClick()
                            showMenu = false
                        },
                        text = { Text(stringResource(R.string.label_management_edit_local_data)) }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onManageEmployeeClick()
                            showMenu = false
                        },
                        text = { Text(stringResource(R.string.label_management_manage_employees)) }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onManageMenuClick()
                            showMenu = false
                        },
                        text = { Text(stringResource(R.string.label_management_manage_menu)) }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onManageSubscriptionClick()
                            showMenu = false
                        },
                        text = { Text(stringResource(R.string.label_management_manage_subscription)) }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onDeleteClick()
                            showMenu = false
                        },
                        text = { Text(stringResource(R.string.label_management_delete_restaurant)) }
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TagsDetailView(tags: List<String>) {
    if (tags.isNotEmpty()) {
        val tagsString = tags.joinToString(separator = ", ")
        DetailItem(
            label = stringResource(R.string.label_restaurant_tags),
            value = tagsString
        )
    }
}

@Composable
fun OutLinedDropdownMenu(
    selectedOption: String,
    itemsList: List<String>,
    onOptionSelected: (String) -> Unit,
    label: String,
    shape: RoundedCornerShape = roundedShape,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            interactionSource = interactionSource,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Hide" else "Show"
                )
            }
        )

        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Release) {
                    expanded = true
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsList.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
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
fun RestaurantCard(
    imageUrl: String,
    name: String,
    location: String
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
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

@Composable
fun IconWithHeader(
    icon: ImageVector,
    text: String,
    scale: Float = 1F
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        Row() {
            Icon(
                imageVector = icon,
                contentDescription = icon.name,
                modifier = Modifier
                    .size(82.dp)
                    .padding(top = 16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(4.dp, 16.dp, 8.dp, 4.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun LogoWithReturn(navController: NavController = rememberNavController()) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier
                .align(Alignment.CenterStart), onClick = { navController.popBackStack() },
            colors = ButtonColors(
                Color.Transparent, Color.Black,
                Color.Transparent, Color.Black
            )
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "back",
                modifier = Modifier.size(35.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Logo(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun ShowErrorToast(context: Context, id: Int) {
    if (id != -1) {
        val msg = stringResource(id)
        println("[TOAST] '$msg'")
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun EmployeeCard(
    employee: RestaurantEmployeeDTO,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
                    onClick = onDeleteClick,
                    imageVector = Icons.Filled.DeleteForever,
                    contentDescription = "DeleteEmployee"
                )
            }
        }

    }
}


@Composable
fun BottomNavigation(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Landing,
        BottomNavItem.Management,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                //label = { Text(item.route) },
                selected = item.route == currentRoute,
                onClick = {
                    if (item.route.isNotBlank() && item.route != currentRoute) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        /*label = {
            Text(text = screen.title)
        },*/

        icon = {
            Icon(
                screen.icon,
                contentDescription = screen.route,
            )
        },

        // Display if the icon it is select or not
        selected = true,

        // Always show the label bellow the icon or not
        alwaysShowLabel = true,

        // Click listener for the icon
        onClick = onClick,

        // Control all the colors of the icon
        colors = NavigationBarItemDefaults.colors()
    )
}


@Composable
fun Heading() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Drawer Item") },
                    selected = false,
                    onClick = { /* Akcja po kliknięciu */ }
                )
                // Dodaj więcej elementów, jeśli są potrzebne
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Box(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .padding(4.dp) // wielkość kolorowego tła
            ) {
                IconButton(onClick = {
                    scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    }
                }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
            }
        }
    }
}

@Composable
fun AddEmployeeDialog(onDismiss: () -> Unit, vm: EmployeeViewModel) {
    var formSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.label_employee_add)) },
        text = {
            Column {
                InputUserInfo(
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
                InputUserInfo(
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
                InputUserInfo(
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
                InputUserInfo(
                    inputText = vm.phoneNum.value,
                    onValueChange = { vm.phoneNum.value = it },
                    label = stringResource(id = R.string.label_phone),
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
                InputUserInfo(
                    inputText = vm.password.value,
                    onValueChange = { vm.password.value = it },
                    label = stringResource(id = R.string.label_password),
                    optional = false,
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
                        checked = vm.isHallEmpployee,
                        onCheckedChange = { isChecked ->
                            vm.isHallEmpployee = isChecked
                        }
                    )
                    Text(stringResource(id = R.string.label_employee_hall))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = vm.isBackdoorEmpployee,
                        onCheckedChange = { isChecked ->
                            vm.isBackdoorEmpployee = isChecked
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
    vm.login.value = employee.login.substringAfter('+')
    vm.firstName.value = employee.firstName
    vm.lastName.value = employee.lastName
    vm.phoneNum.value = employee.phoneNumber
    vm.isHallEmpployee = employee.isHallEmployee
    vm.isBackdoorEmpployee = employee.isBackdoorEmployee

    var formSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.label_employee_edit)) },
        text = {
            Column {
                InputUserInfo(
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
                InputUserInfo(
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
                InputUserInfo(
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
                InputUserInfo(
                    inputText = vm.phoneNum.value,
                    onValueChange = { vm.phoneNum.value = it },
                    label = stringResource(id = R.string.label_phone),
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
                        checked = vm.isHallEmpployee,
                        onCheckedChange = { isChecked ->
                            vm.isHallEmpployee = isChecked
                        }
                    )
                    Text(stringResource(id = R.string.label_employee_hall))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = vm.isBackdoorEmpployee,
                        onCheckedChange = { isChecked ->
                            vm.isBackdoorEmpployee = isChecked
                        }
                    )
                    Text(stringResource(id = R.string.label_employee_backdoor))
                }
            }
        },
        confirmButton = {
            ButtonComponent(
                onClick = {
                    vm.editEmployee(employee)
                    onDismiss()
                },
                label = stringResource(R.string.label_save)
            )
        },
        dismissButton = {
            ButtonComponent(onClick = onDismiss, label = stringResource(id = R.string.label_cancel))
        }
    )
}

@Composable
fun Content() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Zgłodniałeś?")
    }
}

@Composable
fun MenuCard(
    menu: RestaurantMenuDTO,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit
) {

    var showConfirmDeletePopup by remember {
        mutableStateOf(false)
    }

    when {
        showConfirmDeletePopup -> {
            CountDownPopup(
                icon = Icons.Filled.DeleteForever,
                title = stringResource(id = R.string.confirm_delete_title),
                text = stringResource(id = R.string.confirm_delete_text),
                onConfirm = {
                    onDeleteClick()
                    showConfirmDeletePopup = false
                },
                onDismissRequest = { showConfirmDeletePopup = false },
                confirmText = stringResource(id = R.string.label_yes_capital),
                dismissText = stringResource(id = R.string.cancel)
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            Image(
                painterResource(id = R.drawable.ic_logo),
                contentDescription = "",
                modifier = Modifier.fillMaxWidth()
            )

            Row {
                val buttonModifier = Modifier
                    .align(Alignment.Bottom)
                    .size(50.dp)
                    .padding(6.dp)

                Text(
                    text = menu.menuType,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f)
                        .align(Alignment.Bottom)
                )

                SecondaryButton(
                    modifier = buttonModifier,
                    onClick = onEditClick,
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "EditMenuItem"
                )

                SecondaryButton(
                    modifier = buttonModifier,
                    onClick = { showConfirmDeletePopup = true },
                    imageVector = Icons.Filled.DeleteForever,
                    contentDescription = "delete"
                )
            }
        }
    }
}


@Composable
fun MenuItemCard(
    menuItem: RestaurantMenuItemDTO,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            Text(
                text = menuItem.name,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                modifier = Modifier.padding(8.dp)
            )

            Text(
                text = "Price: ${menuItem.price} zł",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )

            if (menuItem.alcoholPercentage != null) {
                Text(
                    text = "Alcohol Percentage: ${menuItem.alcoholPercentage}%",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                SecondaryButton(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(50.dp),
                    onClick = onEditClick,
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "EditMenuItem"
                )

                SecondaryButton(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(50.dp),
                    onClick = onDeleteClick,
                    imageVector = Icons.Filled.DeleteForever,
                    contentDescription = "DeleteMenuItem"
                )
            }
        }
    }
}


@Composable
fun SecondaryButton(
    modifier: Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String,
    contentPadding: PaddingValues = PaddingValues(6.dp),
) {

    val secondaryButtonColors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )

    Button(
        onClick = onClick,
        shape = CircleShape,
        contentPadding = contentPadding,
        colors = secondaryButtonColors,
        modifier = modifier
    ) {
        Icon(
            imageVector,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun CountDownPopup(
    countDownTimer: Int = 5,
    icon: ImageVector,
    title: String,
    text: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit,
) {

    var allowConfirm by remember {
        mutableStateOf(false)
    }

    var timer by remember {
        mutableIntStateOf(countDownTimer)
    }

    if (timer > 0) {
        LaunchedEffect(key1 = timer) {
            delay(1000)
            timer -= 1
            allowConfirm = timer == 0
        }
    }



    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            OutlinedButton(
                onClick = {
                    if (allowConfirm) onConfirm()
                },
                enabled = allowConfirm
            ) {
                if (allowConfirm) {
                    Text(confirmText, color = MaterialTheme.colorScheme.error)
                } else {
                    Text(timer.toString())
                }
            }
        },
        dismissButton = {
            FilledTonalButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(dismissText)
            }
        }
    )
}

@Composable
fun MyFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp)
            .padding(bottom = 56.dp)
            .padding(end = 16.dp)
            .padding(bottom = 16.dp),
        content = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Dodaj"
            )
        }
    )
}

@Composable
fun ProgressBar(currentStep: Int) {
    val progress = when (currentStep) {
        1 -> 0.33f
        2 -> 0.66f
        3 -> 1f
        else -> 0f
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)

    ) {
        Row()
        {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }
    }
}
