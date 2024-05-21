package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.ui.components.MenuItemCard
import com.example.reservant_mobile.ui.viewmodels.MenuItemManagementViewModel


@Composable
fun MenuItemManagementActivity(menuId: Int) {
    val viewmodel = viewModel<MenuItemManagementViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                MenuItemManagementViewModel(menuId = menuId) as T
        }
    )

    LazyColumn (
        modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 8.dp)
    ){
        items(viewmodel.items) { item ->
            MenuItemCard(
                menuItem = item,
                onEditClick = {}, //TODO
                onDeleteClick = { viewmodel.deleteMenu(item) }
            )
        }
    }

}