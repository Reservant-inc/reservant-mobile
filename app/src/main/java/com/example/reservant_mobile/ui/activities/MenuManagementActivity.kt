package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.reservant_mobile.ui.components.AddMenuCard
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
                items(viewmodel.menus) { menu ->
                    MenuCard(
                        menu = menu,
                        onEditClick = viewmodel::editMenu,
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
                item{
                    AddMenuCard(
                        addMenu = {}
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