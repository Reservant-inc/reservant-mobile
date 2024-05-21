package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.IconWithHeader
import com.example.reservant_mobile.ui.components.MenuCard
import com.example.reservant_mobile.ui.components.MyFloatingActionButton
import com.example.reservant_mobile.ui.navigation.RestaurantManagementRoutes
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
        startDestination = RestaurantManagementRoutes.Menu(restaurantId = restaurantId)
    ) {
        composable<RestaurantManagementRoutes.Menu> {
            Box(modifier = Modifier.fillMaxSize()){
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
                                        RestaurantManagementRoutes.MenuItem(menuId = menu.id)
                                    )
                                }
                            }
                        )
                    }
                }
                Box(modifier = Modifier.align(Alignment.BottomEnd)){
                    MyFloatingActionButton(onClick = { }, allPadding = 0.dp)
                }
            }

        }
        composable<RestaurantManagementRoutes.MenuItem> {
            MenuItemManagementActivity(
                menuId = it.toRoute<RestaurantManagementRoutes.MenuItem>().menuId
            )
        }

    }
}