package com.example.reservant_mobile.ui.activities

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
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
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuDTO
import com.example.reservant_mobile.ui.components.AddMenuButton
import com.example.reservant_mobile.ui.components.IconWithHeader
import com.example.reservant_mobile.ui.components.MenuCard
import com.example.reservant_mobile.ui.components.MissingPage
import com.example.reservant_mobile.ui.components.ShowErrorToast
import com.example.reservant_mobile.ui.navigation.RestaurantManagementRoutes
import com.example.reservant_mobile.ui.viewmodels.MenuManagementViewModel
import kotlinx.coroutines.launch


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
                LazyColumn(
                    modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 8.dp)
                ) {
                    item {
                        IconWithHeader(
                            icon = Icons.AutoMirrored.Rounded.MenuBook,
                            text = stringResource(id = R.string.label_menu_management).replace(" ", "\n")
                        )
                    }

                    when {
                        viewmodel.isFetching -> repeat(3){
                            item{
                                MenuCard(
                                    isFetching = true,
                                    name = viewmodel.name,
                                    altName = viewmodel.alternateName,
                                    menuType = viewmodel.menuType,
                                    dateFrom = viewmodel.dateFrom,
                                    dateUntil = viewmodel.dateUntil,
                                    menu = RestaurantMenuDTO(
                                        name = "",
                                        menuType = "",
                                        dateFrom = "",
                                        photo = ""
                                    ),
                                    onEditClick = { },
                                    onDeleteClick = { },
                                    onClick = { },
                                    clearFields = { },
                                    onFilePicked = { }
                                )
                            }
                        }
                        viewmodel.menus.isNotEmpty() -> items(viewmodel.menus) { menu ->

                            val showConfirmDeletePopup = remember { mutableStateOf(false) }
                            val showEditPopup = remember { mutableStateOf(false) }
                            var bitmap by remember { mutableStateOf<Bitmap?>(null) }

                            LaunchedEffect(key1 = Unit) {
                                viewmodel.viewModelScope.launch {
                                    bitmap = viewmodel.getPhoto(menu)
                                }
                            }

                            MenuCard(
                                name = viewmodel.name,
                                altName = viewmodel.alternateName,
                                menuType = viewmodel.menuType,
                                dateFrom = viewmodel.dateFrom,
                                dateUntil = viewmodel.dateUntil,
                                menu = menu,
                                photo = bitmap?.asImageBitmap(),
                                onEditClick = {
                                    viewmodel.viewModelScope.launch {
                                        viewmodel.editMenu(menu)
                                        if (!viewmodel.result.isError){
                                            viewmodel.clearFields()
                                            showEditPopup.value = false
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
                                clearFields = viewmodel::clearFields,
                                onClick = {
                                    if (menu.menuId != null) {
                                        navController.navigate(
                                            RestaurantManagementRoutes.MenuItem(menuId = menu.menuId, restaurantId = restaurantId)
                                        )
                                    }

                                },
                                onFilePicked = { file ->
                                    viewmodel.photo.value = file?.toString() ?: ""
                                },
                                fileErrors = viewmodel.photoErrors(LocalContext.current),
                                fileTooLarge = viewmodel.isPhotoTooLarge(LocalContext.current),
                                isSaving = viewmodel.isSaving,
                                showConfirmDeletePopup = showConfirmDeletePopup,
                                showEditPopup = showEditPopup
                            )

                            if (viewmodel.result.isError){
                                ShowErrorToast(context = LocalContext.current, id = viewmodel.getToastError(viewmodel.result))
                                viewmodel.result.isError = false
                            }

                        }
                        else -> item {
                            MissingPage(errorStringId = viewmodel.getToastError(viewmodel.fetchResult))
                        }
                    }

                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        //.padding(8.dp)
                ){
                    val showAddDialog = remember { mutableStateOf(false) }

                    AddMenuButton(
                        name = viewmodel.name,
                        altName = viewmodel.alternateName,
                        menuType = viewmodel.menuType,
                        dateFrom =  viewmodel.dateFrom,
                        dateUntil = viewmodel.dateUntil,
                        clearFields = viewmodel::clearFields,
                        addMenu = {
                            viewmodel.viewModelScope.launch {
                                viewmodel.addMenu()
                                if (!viewmodel.result.isError){
                                    showAddDialog.value = false
                                }

                            }
                        },
                        onFilePicked = { file ->
                            viewmodel.photo.value = file?.toString() ?: ""
                        },
                        fileErrors = viewmodel.photoErrors(LocalContext.current),
                        fileTooLarge = viewmodel.isPhotoTooLarge(LocalContext.current),
                        isSaving = viewmodel.isSaving,
                        showAddDialog = showAddDialog
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
                menuId = it.toRoute<RestaurantManagementRoutes.MenuItem>().menuId,
                restaurantId = it.toRoute<RestaurantManagementRoutes.MenuItem>().restaurantId
            )
        }

    }
}