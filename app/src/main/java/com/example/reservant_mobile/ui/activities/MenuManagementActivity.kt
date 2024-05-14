package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.CountDownPopup
import com.example.reservant_mobile.ui.components.IconWithHeader
import com.example.reservant_mobile.ui.components.MenuCard
import com.example.reservant_mobile.ui.constants.RestaurantManagementArguments
import com.example.reservant_mobile.ui.constants.RestaurantManagementRoutes
import com.example.reservant_mobile.ui.viewmodels.MenuManagementViewModel


@Composable
fun MenuManagementActivity(restaurantId: Int) {
    val viewmodel = viewModel<MenuManagementViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                MenuManagementViewModel(restaurantId) as T
        }
    )
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RestaurantManagementRoutes.MENU_MANAGE
    ) {
        composable(RestaurantManagementRoutes.MENU_MANAGE) {
            LazyColumn {
                item {
                    IconWithHeader(
                        icon = Icons.AutoMirrored.Rounded.MenuBook,
                        text = stringResource(id = R.string.label_menu_management).replace(" ", "\n")
                    )
                }
                items(viewmodel.menus) { menu ->
                    MenuCard(
                        menu = menu,
                        onEditClick = {}, //TODO
                        onDeleteClick = {
                            viewmodel.deleteMenu(menu)
                        },
                        onClick = {
                            if (menu.id != null) {
                                navController.navigate(
                                    RestaurantManagementRoutes.getMenuItemManageRoute(menu.id)
                                )
                            }
                        }
                    )
                }
            }
        }
        composable(
            RestaurantManagementRoutes.MENU_ITEM_MANAGE, arguments = listOf(
                navArgument(
                    RestaurantManagementArguments.MENU_ID
                ) { type = NavType.IntType })
        ) { backStackEntry ->
            MenuItemManagementActivity(
                menuId = backStackEntry.arguments!!.getInt(RestaurantManagementArguments.MENU_ID)
            )
        }

    }
}