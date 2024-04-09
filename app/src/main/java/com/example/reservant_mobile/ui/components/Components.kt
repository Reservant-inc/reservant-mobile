package com.example.reservant_mobile.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.utils.BottomNavItem
import com.example.reservant_mobile.data.utils.Country
import com.example.reservant_mobile.data.utils.getFlagEmojiFor
import com.example.reservant_mobile.ui.activities.HomeActivity
import com.example.reservant_mobile.ui.activities.RegisterActivity
import com.example.reservant_mobile.ui.theme.Purple40
import com.example.reservant_mobile.ui.theme.Purple80
import com.example.reservant_mobile.ui.theme.PurpleGrey40
import com.example.reservant_mobile.ui.theme.PurpleGrey80
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
    optional: Boolean = false,
    maxLines: Int = 1,
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
) {

    var beginValidation: Boolean by remember {
        mutableStateOf(false)
    }

    var beginValidationOnNextFocus: Boolean by remember {
        mutableStateOf(false)
    }

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
                        if (beginValidationOnNextFocus) beginValidation = true
                        if (it.hasFocus) beginValidationOnNextFocus = true
                    }
            },
            value = inputText,
            onValueChange = onValueChange,
            label = {
                Row {
                    Text(text = label)
                    if (optional)
                        Text(text = " - optional", color = Color.Gray, fontStyle = FontStyle.Italic)
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
            isError = isError && beginValidation,
            maxLines = maxLines,
            leadingIcon = leadingIcon
        )
        if (isError && beginValidation) {
            Text(
                text = errorText,
                color = Color.Red
            )
        }
    }

}
@Composable
fun RestaurantTypeDropdown(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    // Definicja opcji
    val options = listOf(
        stringResource(R.string.label_restaurant_type_restaurant),
        stringResource(R.string.label_restaurant_type_bar),
        stringResource(R.string.label_restaurant_type_cafe)
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(R.string.label_restaurant_type)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            interactionSource = interactionSource,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Zwiń" else "Rozwiń"
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
            options.forEach { option ->
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
fun InputUserFile(
    label: String = "",
    modifier: Modifier = Modifier,
    onFilePicked: (Uri?) -> Unit
) {
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onFilePicked(uri)
    }

    UserButton(
        label = label,
        onClick = {
            pickFileLauncher.launch("*/*")
        },
        modifier = modifier
    )
}

@Composable
fun UserButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String = "",
    isLoading: Boolean = false
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("Button"),
        onClick = onClick,
        content = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    trackColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            } else {
                Text(text = label)
            }
        }
    )
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
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
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
                modifier = Modifier.size(35.dp)
            )
        }
        Logo(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun ErrorResourceText(id: Int) {
    Text(
        color = Color.Red,
        text = if (id != -1) stringResource(id) else ""
    )
}

@Composable
fun BottomNavigation(navController: NavHostController) {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Landing,
        BottomNavItem.Login,
        BottomNavItem.Register
    )

    NavigationBar {
        for (i in items) {
            AddItem(
                screen = i,
                onClick = { navController.navigate(i.title.lowercase()) }
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
        label = {
            Text(text = screen.title)
        },

        icon = {
            Icon(
                screen.icon,
                contentDescription = screen.title,
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

@Preview(showBackground = true)
@Composable
fun Preview() {
    HomeActivity(rememberNavController())
}
