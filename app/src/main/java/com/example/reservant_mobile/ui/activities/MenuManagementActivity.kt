package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.ui.components.MenuCard
import com.example.reservant_mobile.ui.viewmodels.MenuManagementViewModel


@Composable
fun MenuManagementActivity(restaurantId: Int){
    val viewmodel = viewModel<MenuManagementViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T: ViewModel> create(modelClass: Class<T>): T = MenuManagementViewModel(restaurantId) as T
        }
    )

    LazyColumn {
        items(viewmodel.menus){menu ->
            MenuCard(
                menu = menu,
                onEditClick = {}, //TODO
                onDeleteClick = {viewmodel.deleteMenu(menu)}
            )
        }
    }

}