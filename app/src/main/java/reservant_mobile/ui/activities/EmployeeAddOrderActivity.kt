package reservant_mobile.ui.activities

import android.graphics.Bitmap
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.Roles
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.CartItemCard
import reservant_mobile.ui.components.FormInput
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.utils.DefaultResourceProvider
import reservant_mobile.ui.components.MenuItemCard
import reservant_mobile.ui.components.MenuTypeButton
import reservant_mobile.ui.viewmodels.ReservationViewModel
import reservant_mobile.ui.viewmodels.RestaurantDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeAddOrderActivity(
    visitId: Int,
    restaurantId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val reservationViewModel: ReservationViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReservationViewModel(resourceProvider = DefaultResourceProvider(context)) as T
            }
        }
    )

    val restaurantDetailVM = viewModel<RestaurantDetailViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                RestaurantDetailViewModel(restaurantId) as T
        }
    )
    LaunchedEffect(Unit) {
        reservationViewModel.visitId = visitId
        //restaurantDetailVM.loadRestaurantAndMenus(restaurantId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.employee_add_order_title, visitId))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.label_back)
                        )
                    }
                }
            )
        }
    ) {
        EmployeeAddOrderFormContent(
            modifier = Modifier.padding(it),
            reservationViewModel = reservationViewModel,
            restaurantDetailVM = restaurantDetailVM
        )
    }

    // Obsługa dialogów
    reservationViewModel.errorMessage?.let { err ->
        AlertDialog(
            onDismissRequest = { reservationViewModel.errorMessage = null },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(err) },
            confirmButton = {
                ButtonComponent(
                    label = stringResource(R.string.ok),
                    onClick = { reservationViewModel.errorMessage = null }
                )
            }
        )
    }
    reservationViewModel.successMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { reservationViewModel.successMessage = null },
            title = { Text(stringResource(R.string.success_title)) },
            text = { Text(msg) },
            confirmButton = {
                ButtonComponent(
                    label = stringResource(R.string.ok),
                    onClick = { reservationViewModel.successMessage = null }
                )
            }
        )
    }
}


@Composable
fun EmployeeAddOrderFormContent(
    modifier: Modifier = Modifier,
    reservationViewModel: ReservationViewModel,
    restaurantDetailVM: RestaurantDetailViewModel
) {
    val focusManager = LocalFocusManager.current
    val cartItems = reservationViewModel.addedItems

    // Popup z menu
    var isMenuPopupOpen by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(16.dp)) {
        // Notatka
        FormInput(
            inputText = reservationViewModel.note.value,
            onValueChange = { reservationViewModel.note.value = it },
            label = stringResource(R.string.label_write_note),
            optional = true
        )

        Spacer(Modifier.height(8.dp))

        // Dodaj do koszyka
        ButtonComponent(
            label = stringResource(R.string.add_to_cart),
            onClick = {
                focusManager.clearFocus()
                isMenuPopupOpen = true
            }
        )

        Spacer(Modifier.height(16.dp))
        Text(text = stringResource(R.string.label_my_basket), style = MaterialTheme.typography.headlineSmall)

        if (cartItems.isNotEmpty()) {
            LazyColumn {
                items(cartItems.size) { index ->
                    val item = cartItems[index]
                    var menuPhoto by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

                    LaunchedEffect(item.first.photo) {
                        item.first.photo?.let { photo ->
                            menuPhoto = reservationViewModel.getPhoto(photo)
                        }
                    }
                    CartItemCard(
                        item = item,
                        onIncreaseQuantity = { reservationViewModel.increaseItemQuantity(item) },
                        onDecreaseQuantity = { reservationViewModel.decreaseItemQuantity(item) },
                        onRemove = { reservationViewModel.removeItemFromCart(item) },
                        photo = menuPhoto
                    )
                }
            }
        } else {
            Text(stringResource(R.string.label_no_items_in_cart))
        }

        Spacer(Modifier.height(16.dp))
        ButtonComponent(
            label = stringResource(R.string.label_send),
            onClick = {
                focusManager.clearFocus()
                reservationViewModel.viewModelScope.launch {
                    reservationViewModel.createOrder()
                }
            }
        )
        Spacer(Modifier.height(16.dp))
    }

    if (isMenuPopupOpen) {
        FullScreenMenuDialog(
            onDismiss = { isMenuPopupOpen = false },
            reservationViewModel = reservationViewModel,
            restaurantDetailVM = restaurantDetailVM
        )
    }
}


@Composable
fun FullScreenMenuDialog(
    onDismiss: () -> Unit,
    reservationViewModel: ReservationViewModel,
    restaurantDetailVM: RestaurantDetailViewModel
) {
    val menus = restaurantDetailVM.menus ?: emptyList()
    val menuItems = restaurantDetailVM.currentMenu?.menuItems

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Pasek z tytułem i przyciskiem zamknięcia
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.select_menu_items),
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.label_close)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pasek wyboru menu
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    menus.forEach { menu ->
                        MenuTypeButton(
                            menuType = menu.name,
                            onMenuClick = {
                                menu.menuId?.let { menuId ->
                                    restaurantDetailVM.viewModelScope.launch {
                                        restaurantDetailVM.loadFullMenu(menuId)
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Lista elementów menu (scrollowalna)
                if (menuItems.isNullOrEmpty()) {
                    Text(
                        text = stringResource(R.string.no_menus_found),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f) // Scrollowalny koszyk zajmuje resztę przestrzeni
                            .padding(horizontal = 16.dp)
                    ) {
                        items(menuItems.size) { index ->
                            val menuItem = menuItems[index]
                            var menuPhoto by remember { mutableStateOf<Bitmap?>(null) }

                            LaunchedEffect(menuItem.photo) {
                                menuItem.photo?.let { photo ->
                                    menuPhoto = restaurantDetailVM.getPhoto(photo)
                                }
                            }

                            MenuItemCard(
                                menuItem = menuItem,
                                role = Roles.CUSTOMER,
                                getPhoto = { menuPhoto },
                                onInfoClick = { /* Możesz dodać obsługę */ },
                                onAddClick = {
                                    reservationViewModel.addItemToCart(menuItem)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Przycisk zatwierdzenia na dole
                Button(
                    onClick = {
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        }
    }
}


