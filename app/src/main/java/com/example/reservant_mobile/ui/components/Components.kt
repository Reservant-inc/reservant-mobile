package com.example.reservant_mobile.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.graphics.drawable.BitmapDrawable
import android.location.Location
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TakeoutDining
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Info
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.constants.Roles
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.data.services.UserService
import com.example.reservant_mobile.data.utils.BottomNavItem
import com.example.reservant_mobile.data.utils.Country
import com.example.reservant_mobile.data.utils.getFileName
import com.example.reservant_mobile.data.utils.getFlagEmojiFor
import com.example.reservant_mobile.ui.viewmodels.EmployeeViewModel
import com.example.reservant_mobile.ui.viewmodels.RegisterRestaurantViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import kotlin.math.floor

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
    onDismiss: () -> Unit,
    allowFutureDates: Boolean,
    startDate: String
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
        initialSelectedDateMillis = convertDateToMillis(startDate),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return if (allowFutureDates) {
                    true
                } else {
                    utcTimeMillis <= System.currentTimeMillis()
                }
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
fun MyDatePickerDialog(
    onBirthdayChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = { Text(stringResource(R.string.label_register_birthday_select)) },
    startStringValue: String = stringResource(id = R.string.label_register_birthday_dialog),
    allowFutureDates: Boolean = false,
    startDate: String = (LocalDate.now().year - 28).toString() + "-06-15"
) {
    var date by remember { mutableStateOf(startStringValue) }
    var showDatePicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = date,
        onValueChange = { },
        label = label,
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
            onDismiss = { showDatePicker = false },
            allowFutureDates = allowFutureDates,
            startDate = startDate
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

    val items = listOfNotNull(
        BottomNavItem.Home,
        BottomNavItem.Landing,
        BottomNavItem.Management.takeIf { Roles.RESTAURANT_OWNER in UserService.User.roles },
        BottomNavItem.Profile
    )

    var selectedItem by remember { mutableStateOf(items.first()) }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.route.toString()) },
                label = { Text(stringResource(id = item.label)) },
                selected = selectedItem == item,
                alwaysShowLabel = true,
                onClick = {
                    if (selectedItem != item) {
                        navController.navigate(item.route)
                        selectedItem = item
                    }
                }
            )
        }
    }
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
    vm.login.value = employee.login.substringAfter('+')
    vm.firstName.value = employee.firstName
    vm.lastName.value = employee.lastName
    vm.phoneNum.value = employee.phoneNumber
    vm.isHallEmployee = employee.isHallEmployee
    vm.isBackdoorEmployee = employee.isBackdoorEmployee

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
    name: FormField,
    altName: FormField,
    menuType: FormField,
    dateFrom: FormField,
    dateUntil: FormField,
    menu: RestaurantMenuDTO,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    clearFields: () -> Unit,
    onClick: () -> Unit
) {

    var showConfirmDeletePopup by remember { mutableStateOf(false) }
    var showEditPopup by remember { mutableStateOf(false) }


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
                dismissText = stringResource(id = R.string.label_cancel)
            )
        }
        showEditPopup -> {

            name.value = menu.name
            altName.value = menu.alternateName ?: ""
            menuType.value = menu.menuType
            dateFrom.value = menu.dateFrom
            dateUntil.value = menu.dateUntil ?: LocalDate.now().toString()


            AlertDialog(
                onDismissRequest = {
                    showEditPopup = false
                    clearFields()
                },
                title = { Text(text = stringResource(id = R.string.label_edit_menu)) },
                text = {
                    Column {
                        InputUserInfo(
                            label = stringResource(id = R.string.label_restaurant_name),
                            inputText = name.value,
                            onValueChange = {name.value = it}
                        )
                        InputUserInfo(
                            label = stringResource(id = R.string.label_alternate_name),
                            inputText = altName.value,
                            onValueChange = {altName.value = it}
                        )
                        InputUserInfo(
                            label = stringResource(id = R.string.label_menu_type),
                            inputText = menuType.value,
                            onValueChange = {menuType.value = it}
                        )
                        MyDatePickerDialog (
                            label = { Text(text = stringResource(id = R.string.label_date_from))},
                            allowFutureDates = true,
                            startStringValue = dateFrom.value,
                            startDate = dateFrom.value,
                            onBirthdayChange = {dateFrom.value = it}
                        )
                        MyDatePickerDialog (
                            label = { Text(text = stringResource(id = R.string.label_date_to))},
                            allowFutureDates = true,
                            startStringValue = dateUntil.value,
                            startDate = dateUntil.value,
                            onBirthdayChange = {dateUntil.value = it}
                        )

                    }
                },
                dismissButton = {
                    ButtonComponent(
                        onClick = {
                            showEditPopup = false
                            clearFields()
                        },
                        label = stringResource(id = R.string.label_cancel)
                    )
                },
                confirmButton = {
                    ButtonComponent(
                        onClick = {
                            showEditPopup = false
                            onEditClick()
                            clearFields()
                        },
                        label = stringResource(id = R.string.label_save)
                    )
                },

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
                    onClick = {showEditPopup = true},
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
fun AddMenuButton(
    name: FormField,
    altName: FormField,
    menuType: FormField,
    dateFrom: FormField,
    dateUntil: FormField,
    clearFields: () -> Unit,
    addMenu: () -> Unit
){
    var showAddDialog by remember { mutableStateOf(false)}

    when{
        showAddDialog -> {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    clearFields()
                },
                title = { Text(text = stringResource(id = R.string.label_add_menu)) },
                text = {
                    Column {
                        InputUserInfo(
                            label = stringResource(id = R.string.label_restaurant_name),
                            inputText = name.value,
                            onValueChange = {name.value = it}
                        )
                        InputUserInfo(
                            label = stringResource(id = R.string.label_alternate_name),
                            inputText = altName.value,
                            optional = true,
                            onValueChange = {altName.value = it}
                        )
                        InputUserInfo(
                            label = stringResource(id = R.string.label_menu_type),
                            inputText = menuType.value,
                            onValueChange = {menuType.value = it}
                        )
                        MyDatePickerDialog (
                            label = { Text(text = stringResource(id = R.string.label_date_from))},
                            allowFutureDates = true,
                            startStringValue = dateFrom.value,
                            startDate = LocalDate.now().toString(),
                            onBirthdayChange = {dateFrom.value = it}
                        )
                        MyDatePickerDialog (
                            label = { Text(text = stringResource(id = R.string.label_date_to))},
                            allowFutureDates = true,
                            startStringValue = dateUntil.value,
                            startDate = LocalDate.now().toString(),
                            onBirthdayChange = {dateUntil.value = it}
                        )

                    }
                },
                dismissButton = {
                    ButtonComponent(
                        onClick = {
                            showAddDialog = false
                            clearFields()
                        },
                        label = stringResource(id = R.string.label_cancel)
                    )
                },
                confirmButton = {
                    ButtonComponent(
                        onClick = {
                            showAddDialog = false
                            addMenu()
                            clearFields()
                        },
                        label = stringResource(id = R.string.label_save)
                    )
                },
            )
        }
    }

    MyFloatingActionButton(
        onClick = { showAddDialog = true }
    )
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
fun DeletePopup(
    icon: ImageVector,
    title: String,
    text: String,
    confirmText:String = "Confirm",
    dismissText: String = "Cancel",
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit,
    enabled: Boolean = true,
    deleteButtonContent: @Composable (RowScope.() -> Unit) = {Text(confirmText, color = MaterialTheme.colorScheme.error)}
){
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
                onClick = onConfirm,
                enabled = enabled,
                content = deleteButtonContent
            )
        },
        dismissButton = {
            FilledTonalButton(
                onClick = onDismissRequest
            ) {
                Text(dismissText)
            }
        }
    )
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
    
    /*AlertDialog(
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
    )*/
    
    DeletePopup(
        icon = icon,
        title = title,
        text = text,
        dismissText = dismissText,
        onDismissRequest = onDismissRequest,
        onConfirm = {
            if (allowConfirm) onConfirm()
        },
        enabled = allowConfirm
    ){
        if (allowConfirm) {
            Text(confirmText, color = MaterialTheme.colorScheme.error)
        } else {
            Text(timer.toString())
        }
    }
}

@Composable
fun MyFloatingActionButton(
    onClick: () -> Unit,
    allPadding: Dp = 16.dp,
    topPadding: Dp = 16.dp,
    bottomPadding: Dp = 16.dp,
    startPadding: Dp = 16.dp,
    endPadding: Dp = 16.dp,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .padding(allPadding)
            .padding(top = topPadding, bottom = bottomPadding, start = startPadding, end = endPadding),
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

@Composable
fun RatingBar(rating: Float) {
    val fullStars = floor(rating).toInt()
    val halfStars = if (rating - fullStars >= 0.5) 1 else 0
    val emptyStars = 5 - fullStars - halfStars

    Row {
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Filled Star"
            )
        }
        repeat(halfStars) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.StarHalf,
                contentDescription = "Half Star"
            )
        }
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Filled.StarBorder,
                contentDescription = "Empty Star"
            )
        }
    }
}

@Composable
fun MenuTypeButton(modifier: Modifier = Modifier, menuType: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
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

@Composable
fun MenuCategoryButton(modifier: Modifier = Modifier, category: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = modifier.padding(2.dp)
    ) {
        Text(category)
    }
}

@Composable
fun TagSelectionScreen(vm: RegisterRestaurantViewModel, onDismiss: () -> Unit, onTagSelected: (String, Boolean) -> Unit,) {
    val selectedTags = vm.selectedTags
    val tags = vm.tags

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wybierz tagi") },
        text = {
            LazyColumn {
                items(tags) { tag ->
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
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("OK")
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagList(tags: List<String>) {
    FlowRow(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        tags.forEach { tag ->
            TagItem(tag = tag)
        }
    }
}

@Composable
fun MenuItemCard(
    name: String,
    price: String,
    description: String,
    imageResource: Int,
    onInfoClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onInfoClick,
                        modifier = Modifier
                            .size(24.dp)
                            .offset(y = (-4).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = price,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.offset(y = (-4).dp)
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = painterResource(imageResource),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxSize()
            )
            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun FullscreenGallery(onDismiss: () -> Unit) {
    val imageList = listOf(
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo,
        R.drawable.restaurant_photo
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 64.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(imageList.size) { index ->
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(1f),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = imageList[index]),
                                contentDescription = "Image $index",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingActionMenu(
    onDineInClick: () -> Unit,
    onDeliveryClick: () -> Unit,
    onTakeawayClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(onClick = { expanded = !expanded }) {
            Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Plecak")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    onDineInClick()
                    expanded = false
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.LocalDining, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Na miejscu")
                    }
                }
            )
            DropdownMenuItem(
                onClick = {
                    onDeliveryClick()
                    expanded = false
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.DeliveryDining, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dostawa")
                    }
                }
            )
            DropdownMenuItem(
                onClick = {
                    onTakeawayClick()
                    expanded = false
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.TakeoutDining, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Odbiór")
                    }
                }
            )
        }
    }
}

@Composable
fun TagItem(tag: String) {
    Text(
        text = tag,
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 12.sp,
        modifier = Modifier
            .padding(4.dp)
            .background(
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
fun TabRowSwitch(
    pages: Map<Int, Pair<String, @Composable () -> Unit>>
) {
    val pagerState = rememberPagerState(
        pageCount = {pages.size}
    )
    val coroutineScope = rememberCoroutineScope()
    val cornerShape = RoundedCornerShape(50)

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .padding(30.dp)
                .clip(cornerShape),
            indicator = {},
            divider = {}
        ) {
            pages.forEach{ (index, tabItem) ->
                val selected = pagerState.currentPage == index
                Tab(
                    modifier =
                    if (selected) Modifier
                        .clip(cornerShape)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .background(MaterialTheme.colorScheme.background)

                    else Modifier
                        .clip(cornerShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),

                    selected = selected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = tabItem.first) }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) {page ->
            pages[page]?.second?.invoke()
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(mapView: MapView): MapView {
    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
    }

@SuppressLint("UnrememberedMutableState")
@Composable
fun MainMapView(
    mapView: MapView,
    startPoint: GeoPoint
) {

    val geoPoint by mutableStateOf(startPoint)
    val mapViewState = rememberMapViewWithLifecycle(mapView)

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapViewState },
        update = {
            view -> view.controller.setCenter(geoPoint)
        }
    )

}