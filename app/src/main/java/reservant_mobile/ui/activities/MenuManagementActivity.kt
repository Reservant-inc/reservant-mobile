package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.RestaurantMenuDTO
import reservant_mobile.ui.components.AddMenuButton
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.MenuCard
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.components.ShowErrorToast
import reservant_mobile.ui.navigation.RestaurantManagementRoutes
import reservant_mobile.ui.viewmodels.MenuManagementViewModel


@Composable
fun MenuManagementActivity(
    onReturnClick: () -> Unit,
    restaurantId: Int
) {
    val viewmodel = viewModel<MenuManagementViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                MenuManagementViewModel(restaurantId) as T
        }
    )
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RestaurantManagementRoutes.Menu(restaurantId = restaurantId)
    ) {

        composable<RestaurantManagementRoutes.Menu> {
            Box(modifier = Modifier.fillMaxSize()){
                LazyColumn(
                    modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 8.dp)
                ) {
                    item {
                        IconWithHeader(
                            icon = Icons.AutoMirrored.Rounded.MenuBook,
                            text = stringResource(id = R.string.label_menu_management),
                            showBackButton = true,
                            onReturnClick = onReturnClick
                        )
                    }

                    when {
                        viewmodel.isFetching -> repeat(3){
                            item{
                                MenuCard(
                                    name = viewmodel.name,
                                    altName = viewmodel.alternateName,
                                    menuType = viewmodel.menuType,
                                    menuTypes = emptyList(),
                                    dateFrom = viewmodel.dateFrom,
                                    dateUntil = viewmodel.dateUntil,
                                    menu = RestaurantMenuDTO(
                                        name = "",
                                        menuType = "",
                                        dateFrom = "",
                                    ),
                                    onEditClick = { },
                                    onDeleteClick = { },
                                    clearFields = { },
                                    onClick = { },
                                    isFetching = true
                                )
                            }
                        }
                        viewmodel.menus.isNotEmpty() -> items(viewmodel.menus) { menu ->

                            val showConfirmDeletePopup = remember { mutableStateOf(false) }
                            val showEditPopup = remember { mutableStateOf(false) }

                            MenuCard(
                                name = viewmodel.name,
                                altName = viewmodel.alternateName,
                                menuType = viewmodel.menuType,
                                dateFrom = viewmodel.dateFrom,
                                dateUntil = viewmodel.dateUntil,
                                menu = menu,
                                onEditClick = {
                                    if (viewmodel.isMenuValid()){
                                        viewmodel.viewModelScope.launch {

                                            viewmodel.editMenu(menu)
                                            if (!viewmodel.result.isError){
                                                viewmodel.clearFields()
                                                showEditPopup.value = false
                                            }
                                        }
                                    }
                                },
                                onDeleteClick = {
                                    viewmodel.viewModelScope.launch {
                                        menu.menuId?.let { id -> viewmodel.deleteMenu(id) }
                                        if (!viewmodel.result.isError){
                                            showConfirmDeletePopup.value = false
                                        }
                                    }
                                },
                                onClick = {
                                    if (menu.menuId != null) {
                                        navController.navigate(
                                            RestaurantManagementRoutes.MenuItem(menuId = menu.menuId, restaurantId = restaurantId)
                                        )
                                    }

                                },
                                isSaving = viewmodel.isSaving,
                                showConfirmDeletePopup = showConfirmDeletePopup,
                                showEditPopup = showEditPopup,
                                isNameInvalid = viewmodel.isNameInvalid(),
                                isAltNameInvalid = viewmodel.isAltNameInvalid(),
                                isMenuTypeInvalid = viewmodel.isMenuTypeInvalid(),
                                menuTypes = viewmodel.menuTypes,
                                clearFields = { viewmodel.clearFields() },
                                areDatesInvalid = viewmodel.areDatesInvalid(),
                                isDateUntilInvalid = viewmodel.isDateUntilInvalid()
                            )

                            if (viewmodel.result.isError){
                                ShowErrorToast(context = LocalContext.current, id = viewmodel.getToastError(viewmodel.result))
                                viewmodel.result.isError = false
                            }

                        }
                        viewmodel.menus.isEmpty() -> item {
                            MissingPage(errorStringId = R.string.error_not_found)
                        }
                        else -> item {
                            MissingPage(errorStringId = viewmodel.getToastError(viewmodel.fetchResult))
                        }
                    }

                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ){
                    val showAddDialog = remember { mutableStateOf(false) }

                    AddMenuButton(
                        name = viewmodel.name,
                        altName = viewmodel.alternateName,
                        menuType = viewmodel.menuType,
                        dateFrom =  viewmodel.dateFrom,
                        dateUntil = viewmodel.dateUntil,
                        clearFields = viewmodel::clearFields,
                        isSaving = viewmodel.isSaving,
                        showAddDialog = showAddDialog,
                        isNameInvalid = viewmodel.isNameInvalid(),
                        isAltNameInvalid = viewmodel.isAltNameInvalid(),
                        isMenuTypeInvalid = viewmodel.isMenuTypeInvalid(),
                        areDatesInvalid = viewmodel.areDatesInvalid(),
                        isDateUntilInvalid = viewmodel.isDateUntilInvalid(),
                        menuTypes = viewmodel.menuTypes,
                        addMenu = {
                            if (viewmodel.isMenuValid()){
                                viewmodel.viewModelScope.launch {
                                    viewmodel.addMenu()
                                    if (!viewmodel.result.isError){
                                        showAddDialog.value = false
                                    }

                                }
                            }
                        }
                    )

                    if (viewmodel.result.isError){
                        ShowErrorToast(context = LocalContext.current, id = viewmodel.getToastError(viewmodel.result))
                        viewmodel.result.isError = false
                    }
                }


            }

        }

        composable<RestaurantManagementRoutes.MenuItem> {
            MenuItemManagementActivity(
                onReturnClick = { navController.popBackStack() },
                menuId = it.toRoute<RestaurantManagementRoutes.MenuItem>().menuId,
                restaurantId = it.toRoute<RestaurantManagementRoutes.MenuItem>().restaurantId
            )
        }

    }
}