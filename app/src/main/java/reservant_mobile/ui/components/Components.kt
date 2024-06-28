package reservant_mobile.ui.components

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import reservant_mobile.data.models.dtos.RestaurantMenuDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.BottomNavItem
import reservant_mobile.ui.viewmodels.EmployeeViewModel
import reservant_mobile.ui.viewmodels.RestaurantViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.floor

val roundedShape = RoundedCornerShape(12.dp)

//TODO: co z tym komponentem a comboboxem
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
fun Logo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_logo),
        contentDescription = "Logo",
        modifier = modifier.size(120.dp)
    )
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
    DetailItem(
        label = stringResource(R.string.label_restaurant_tags),
        value = tags.joinToString(separator = ", ")
    )
}

@Composable
fun IconWithHeader(
    icon: ImageVector,
    text: String,
    showBackButton: Boolean = false,
    onReturnClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            if (showBackButton){
                ReturnButton(
                    onReturnClick = onReturnClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            Row (
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .align(Alignment.Center)
            ){
                Icon(
                    imageVector = icon,
                    contentDescription = icon.name,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        }

        HorizontalDivider(thickness = 2.dp)
    }
}

@Composable
fun LogoWithReturn(
    onReturnClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        ReturnButton(
            onReturnClick = onReturnClick,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Logo(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun ReturnButton(
    onReturnClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Button(
        onClick = onReturnClick,
        contentPadding = PaddingValues(2.dp),
        colors = ButtonColors(
            Color.Transparent, Color.Black,
            Color.Transparent, Color.Black
        ),
        shape = RectangleShape,
        modifier = modifier
    ) {
        Icon(
            Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = "back",
            modifier = Modifier.size(35.dp),
            tint = MaterialTheme.colorScheme.primary
        )
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
    var showConfirmDeletePopup by remember { mutableStateOf(false) }

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
                    onClick = {showConfirmDeletePopup = true},
                    imageVector = Icons.Filled.DeleteForever,
                    contentDescription = "DeleteEmployee"
                )
            }
        }

    }
}


@Composable
fun BottomNavigation(
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>
) {

    val items = listOfNotNull(
        BottomNavItem.Home,
        BottomNavItem.Landing,
        BottomNavItem.Management.takeIf { Roles.RESTAURANT_OWNER in UserService.User.roles },
        BottomNavItem.Profile
    )

    var selectedItem by remember { mutableStateOf(items.first()) }
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    var outlineColor by remember { mutableStateOf(outlineVariant) }

    if (isSystemInDarkTheme()){
        outlineColor = MaterialTheme.colorScheme.outline
    }


    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.drawBehind {
                drawLine(
                    color = outlineColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.5f
                )
                }
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
    vm.clearFields()
    var formSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

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
                FormInput(
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
                FormInput(
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
                FormInput(
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


fun Modifier.shimmer(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    val transition = rememberInfiniteTransition(label = "Loading animation")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = "Loading animation"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5),
                Color(0xFF8F8B8B),
                Color(0xFFB8B5B5),
            ),
            start = Offset(startOffsetX, 0F),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        ),
        shape = ShapeDefaults.Medium
    )
        .onGloballyPositioned {
            size = it.size
        }
}

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

    var expanded = remember {
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
                    onValueChange = { menuType.value = it},
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBox(
    expanded: MutableState<Boolean>,
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    label: String,
    isError: Boolean = false,
    errorText: String = ""
){

    val onDismiss = { expanded.value = false }
    var beginValidation by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
            expanded.value = !expanded.value
            beginValidation = true
        }
    ) {
        Column {
            OutlinedTextField(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .menuAnchor(),
                label = { Text(text = label) },
                value = value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                shape = RoundedCornerShape(8.dp),
                isError = isError && beginValidation
            )

            if (isError && beginValidation) Text(text = errorText, color = MaterialTheme.colorScheme.error)
        }

        ExposedDropdownMenu(
            modifier = Modifier.exposedDropdownSize(matchTextFieldWidth = false),
            expanded = expanded.value,
            onDismissRequest = onDismiss
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(text = it) },
                    onClick = {
                        onValueChange(it)
                        onDismiss()
                    }
                )
            }
        }
    }
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
            CountDownPopup(
                icon = Icons.Filled.DeleteForever,
                title = stringResource(id = R.string.confirm_delete_title),
                text = stringResource(id = R.string.confirm_delete_text),
                onConfirm = {
                    onDeleteClick()
                },
                onDismissRequest = {showConfirmDeletePopup.value = false},
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
                hide = {showEditPopup.value = false},
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
            if (photo != null){
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
            }



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
fun MenuItemCard(
    name: FormField,
    altName: FormField,
    price: FormField,
    alcoholPercentage: FormField,
    photo: FormField,
    menuItem: RestaurantMenuItemDTO,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    clearFields: () -> Unit,
    context: Context
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

            name.value = menuItem.name
            altName.value = menuItem.alternateName ?: ""
            price.value = menuItem.price.toString()
            alcoholPercentage.value = menuItem.alcoholPercentage.toString()
            photo.value = menuItem.photoFileName ?: ""

            MenuItemPopup(
                title = { Text(text = stringResource(id = R.string.label_edit_menu_item)) },
                hide = { showEditPopup = false },
                onConfirm = onEditClick,
                clear = clearFields,
                name = name,
                altName = altName,
                price = price,
                alcoholPercentage = alcoholPercentage,
                photo = photo,
                context = context
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row {
            Column {
                Text(
                    text = menuItem.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(0.5f)
                )

                if (menuItem.alternateName != null) {
                    Text(
                        text = menuItem.alternateName,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                }

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
                    horizontalArrangement = Arrangement.Start
                ) {
                    SecondaryButton(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(50.dp),
                        onClick = { showEditPopup = true },
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "EditMenuItem"
                    )

                    SecondaryButton(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(50.dp),
                        onClick = { showConfirmDeletePopup = true },
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = "DeleteMenuItem"
                    )
                }
            }

            //TODO podmienić na zdj z backendu
            Image(
                painterResource(id = R.drawable.pizza),
                contentDescription = "",
                modifier = Modifier.fillMaxHeight()
            )

        }
    }
}

@Composable
fun MenuItemPopup(
    title: @Composable (() -> Unit),
    hide: () -> Unit,
    onConfirm: () -> Unit,
    clear: () -> Unit,
    name: FormField,
    altName: FormField,
    price: FormField,
    alcoholPercentage: FormField,
    photo: FormField,
    context: Context
) {
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
                    onValueChange = { name.value = it }
                )
                FormInput(
                    label = stringResource(id = R.string.label_alternate_name),
                    optional = true,
                    inputText = altName.value,
                    onValueChange = { altName.value = it }
                )
                FormInput(
                    label = stringResource(id = R.string.label_price),
                    inputText = price.value,
                    onValueChange = { price.value = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                FormInput(
                    label = stringResource(id = R.string.label_alcohol),
                    inputText = alcoholPercentage.value,
                    onValueChange = { alcoholPercentage.value = it },
                    optional = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                FormFileInput(
                    label = stringResource(id = R.string.label_menu_item_photo),
                    onFilePicked = { file -> photo.value = file.toString() },
                    context = context
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
                    hide()
                    onConfirm()
                    clear()
                },
                label = stringResource(id = R.string.label_save)
            )
        },

        )
}

@Composable
fun AddMenuItemButton(
    name: FormField,
    altName: FormField,
    price: FormField,
    alcoholPercentage: FormField,
    photo: FormField,
    clearFields: () -> Unit,
    addMenu: () -> Unit,
    context: Context
) {
    var showAddDialog by remember { mutableStateOf(false) }

    when {
        showAddDialog -> {
            MenuItemPopup(
                title = { Text(text = stringResource(id = R.string.label_edit_menu_item)) },
                hide = { showAddDialog = false },
                onConfirm = addMenu,
                clear = clearFields,
                name = name,
                altName = altName,
                price = price,
                alcoholPercentage = alcoholPercentage,
                photo = photo,
                context = context
            )
        }
    }

    MyFloatingActionButton(
        onClick = { showAddDialog = true }
    )
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
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit,
    enabled: Boolean = true,
    deleteButtonContent: @Composable (RowScope.() -> Unit) = {
        Text(
            confirmText,
            color = MaterialTheme.colorScheme.error
        )
    }
) {
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
    isSaving: Boolean = false
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
    ) {
        if (!allowConfirm) {
            Text(timer.toString())
        } else if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(25.dp)
            )
        } else {
            Text(confirmText, color = MaterialTheme.colorScheme.error)
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
            .padding(
                top = topPadding,
                bottom = bottomPadding,
                start = startPadding,
                end = endPadding
            ),
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
fun TagSelectionScreen(
    vm: RestaurantViewModel,
    onDismiss: () -> Unit,
    onTagSelected: (String, Boolean) -> Unit,
) {
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
fun TagList(tags: List<String>, onRemoveTag: (String) -> Unit) {
    FlowRow(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        tags.forEach { tag ->
            TagItem(tag = tag, onRemove = { onRemoveTag(tag) })
        }
    }
}

@Composable
fun TagItem(
    tag: String,
    onRemove: () -> Unit = {},
    removable: Boolean = true
) {
    InputChip(
        onClick = { onRemove() },
        label = { Text(tag) },
        trailingIcon = {
            if(removable){
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove tag"
                )
            }
        },
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(4.dp),
        selected = false
    )
}


@Composable
fun MenuItemCard(
    menuItem: RestaurantMenuItemDTO,
    role: String,
    name: String,
    altName: String,
    price: String,
    photo: Int,
    onInfoClick: () -> Unit,
    onAddClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = menuItem.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.label_menu_price) + ": ${menuItem.price} zł",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (menuItem.alcoholPercentage != null) {
                        Text(
                            text = "Alcohol Percentage: ${menuItem.alcoholPercentage}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        when (role) {
                            Roles.CUSTOMER -> {
                                IconButton(
                                    onClick = onInfoClick,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = "Info",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = onAddClick,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddShoppingCart,
                                        contentDescription = "Add",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Roles.RESTAURANT_OWNER -> {
                                IconButton(
                                    onClick = onEditClick,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Edit",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = onDeleteClick,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                }

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
                Image(
                    painter = painterResource(photo),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxSize()
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
        modifier = Modifier.fillMaxSize()
    ) {

        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(onClick = { expanded = false })
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .height(680.dp)
                        .width(360.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    FloatingTabSwitch(
                        pages = listOf(
                            "Na miejscu" to {
                                DineInContent(
                                    onDineInClick,
                                    modifier = Modifier.padding(top = 88.dp)
                                ) },
                            "Dostawa" to { // TODO: not implemented on backend
                                DeliveryContent(
                                    onDeliveryClick,
                                    modifier = Modifier.padding(top = 88.dp)
                                ) },
                            "Odbiór" to { // TODO: not implemented on backend
                                TakeawayContent(
                                    onTakeawayClick,
                                    modifier = Modifier.padding(top = 88.dp)
                                ) }
                        ),
                        paneScroll = false
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(onClick = { expanded = !expanded }) {
                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Plecak")
            }
        }
    }
}

// TODO: resources
@Composable
fun DineInContent(
    onDineInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var comment by remember { mutableStateOf("") }
    var seats by remember { mutableIntStateOf(1) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Moja rezerwacja",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
        )
        MyDatePickerDialog(
            label = { Text("Data rezerwacji") },
            onDateChange = { selectedDate ->
                // TODO: date change
            },
            startDate = LocalDate.now().toString(),
            allowFutureDates = true
        )

        Text(
            text = "Liczba miejsc",
            style = MaterialTheme.typography.bodyLarge
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            LabelButton(
                onClick = { if (seats > 1) seats-- },
                color = MaterialTheme.colorScheme.primary,
                enabled = seats > 1,
                label = "-"
            )
            Text(
                text = seats.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            LabelButton(
                onClick = { if (seats < 10) seats++ },
                color = MaterialTheme.colorScheme.primary,
                enabled = seats < 10,
                label = "+"
            )
        }
        OutlinedTextField(
            value = "",
            onValueChange = { /* TODO: Handle note change */ },
            label = { Text(text = "Napisz notatkę do zamówienia...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(8.dp)
        )

        Text(
            text = "Mój koszyk",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    .padding(start = 16.dp, end = 16.dp)
                    .padding(vertical = 8.dp)
                ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Danie1", style = MaterialTheme.typography.bodyLarge)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "ilość: 1", style = MaterialTheme.typography.bodyLarge)
                        IconButton(
                            onClick = { /* TODO: Decrease item count */ },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Remove")
                        }
                        IconButton(
                            onClick = { /* TODO: Increase item count */ },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                }
                Text(
                    text = "Kwota: 30zł",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        OutlinedTextField(
            value = "JSKS6X293",
            onValueChange = { /* TODO: Change promo code */ },
            label = {
                Text(
                    text = "Wpisz kod promocyjny",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)

        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Kwota całkowita:",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "60zł",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = { /* TODO: Go to summary */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text(text = "Przejdź do podsumowania")
        }
    }
}

@Composable
fun LabelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    label: String
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp)
            .background(color, CircleShape)
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DeliveryContent(
    onDeliveryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "Dostawa", style = MaterialTheme.typography.headlineSmall)

        Button(onClick = onDeliveryClick) {
            Text("Zamów dostawę")
        }
    }
}

@Composable
fun TakeawayContent(
    onTakeawayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, end = 8.dp, start = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mój koszyk",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Danie1", style = MaterialTheme.typography.bodyLarge)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "ilość: 1", style = MaterialTheme.typography.bodyLarge)
                                IconButton(
                                    onClick = { /* TODO: Decrease item count */ },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Remove")
                                }
                                IconButton(
                                    onClick = { /* TODO: Increase item count */ },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                                }
                            }
                        }
                        Text(
                            text = "Kwota: 30zł",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = "",
            onValueChange = { /* TODO: Handle note change */ },
            label = { Text(text = "Napisz notatkę do zamówienia...") },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = "JSKS6X293",
            onValueChange = { /* TODO: Change promo code */ },
            label = {
                Text(
                    text = "Wpisz kod promocyjny",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Kwota całkowita:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "60zł",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { /* TODO: Go to summary */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text(text = "Przejdź do podsumowania")
            }
        }
    }
}

// TODO: verify colors
@Composable
fun SearchBarWithFilter() {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 36.dp),
                singleLine = true,
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp)
            )
            if (text.isEmpty()) {
                Text(
                    text = "Szukaj...",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        IconButton(
            onClick = { /* TODO: Handle filter action */ },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}


@Composable
fun FloatingTabSwitch(
    pages: List<Pair<String, @Composable () -> Unit>>,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    paneScroll: Boolean = true
) {
    val pagerState = rememberPagerState(
        pageCount = { pages.size }
    )
    val coroutineScope = rememberCoroutineScope()
    val cornerShape = RoundedCornerShape(50)


    val indicator = @Composable { tabPositions: List<TabPosition> ->
        CustomIndicator(tabPositions, pagerState)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = paneScroll
        ) { page ->
            pages[page].second.invoke()
        }
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = color,
            modifier = Modifier
                .padding(20.dp)
                .clip(cornerShape),
            indicator = indicator,
            divider = {}
        ) {
            pages.forEachIndexed { index, tabItem ->
                val selected = pagerState.currentPage == index
                Tab(
                    modifier = Modifier.zIndex(6f),
                    text = {
                        if (selected) {
                            Text(text = tabItem.first, color = MaterialTheme.colorScheme.background)
                        } else {
                            Text(text = tabItem.first)
                        }
                    },
                    selected = selected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun CustomIndicator(tabPositions: List<TabPosition>, pagerState: PagerState) {
    val transition = updateTransition(pagerState.currentPage, label = "")
    val indicatorStart by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 400f)
            } else {
                spring(dampingRatio = 1f, stiffness = 1000f)
            }
        }, label = ""
    ) {
        tabPositions[it].left
    }

    val indicatorEnd by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 1000f)
            } else {
                spring(dampingRatio = 1f, stiffness = 400f)
            }
        }, label = ""
    ) {
        tabPositions[it].right
    }

    Box(
        Modifier
            .offset(x = indicatorStart)
            .wrapContentSize(align = Alignment.BottomStart)
            .width(indicatorEnd - indicatorStart)
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
            .zIndex(5f)
    )
}

@Composable
fun ImageCard(
    image: Painter
){
    Card(
        modifier = Modifier.size(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
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

@Composable
fun OsmMapView(
    mapView: MapView,
    startPoint: GeoPoint,
    modifier: Modifier = Modifier.fillMaxSize()
) {

    val geoPoint by remember { mutableStateOf(startPoint) }
    val mapViewState = rememberMapViewWithLifecycle(mapView)

    AndroidView(
        modifier = modifier,
        factory = { mapViewState },
        update = { view ->
            view.controller.setCenter(geoPoint)
        }
    )
}

@Composable
fun MissingPage(
    errorStringId: Int,
    modifier:Modifier = Modifier
        .fillMaxSize()
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Icon(
            modifier = Modifier
                .height(100.dp)
                .width(100.dp),
            imageVector = Icons.Rounded.Error,
            contentDescription = "Missing page error",
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = if (errorStringId != -1) stringResource(id = errorStringId) else ""
        )
    }
}

@Composable
fun OrderItem(order: OrderDTO) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(text = stringResource(id = R.string.label_date)+": ${order.date}")
        Text(text = stringResource(id = R.string.label_total_cost)+": ${order.cost}")
        Text(text = order.customer)
        Text(text = order.status, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(32.dp)
            )
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
    HorizontalDivider()
}

@Composable
fun FilterDialog(
    onDismissRequest: () -> Unit,
    onFilterSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(id = R.string.label_filters)) },
        text = {
            Column {
                FilterOption("Odebrano", onFilterSelected)
                FilterOption("Anulowano", onFilterSelected)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.label_close))
            }
        }
    )
}

@Composable
fun FilterOption(status: String, onFilterSelected: (String) -> Unit) {
    TextButton(onClick = { onFilterSelected(status) }) {
        Text(text = status)
    }
}