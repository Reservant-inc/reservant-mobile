package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FoodBank
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.Roles
import reservant_mobile.ui.components.AddMenuItemButton
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.MenuItemCard
import reservant_mobile.ui.viewmodels.MenuItemManagementViewModel


@Composable
fun MenuItemManagementActivity(onReturnClick: () -> Unit ,menuId: Int, restaurantId: Int) {
    val viewmodel = viewModel<MenuItemManagementViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                MenuItemManagementViewModel(menuId = menuId, restaurantId = restaurantId) as T
        }
    )
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(
            modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 8.dp)
        ) {
            item {
                IconWithHeader(
                    icon = Icons.Rounded.FoodBank,
                    text = stringResource(id = R.string.label_menu_item_management),
                    showBackButton = true,
                    onReturnClick = onReturnClick
                )
            }
            items(viewmodel.items) { item ->
                MenuItemCard(
                    name = viewmodel.name,
                    altName = viewmodel.alternateName,
                    price = viewmodel.price,
                    alcoholPercentage = viewmodel.alcoholPercentage,
                    //photo = viewmodel.photo, // TODO: fetchowanie zdjec menu
                    menuItem = item,
                    onEditClick = {
                        viewmodel.viewModelScope.launch {
                            viewmodel.editMenuItem(item, context)
                        }
                    },
                    onDeleteClick = {
                        viewmodel.viewModelScope.launch {
                            item.menuItemId?.let { id -> viewmodel.deleteMenuItem(id) }
                        }
                    },
                    clearFields = viewmodel::clearFields,
                    role = Roles.RESTAURANT_OWNER,
                    context = context
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
            //.padding(8.dp)
        ){
            AddMenuItemButton(
                name = viewmodel.name,
                altName = viewmodel.alternateName,
                price = viewmodel.price,
                alcoholPercentage =  viewmodel.alcoholPercentage,
                photo = viewmodel.photo,
                clearFields = viewmodel::clearFields,
                addMenu = {
                    viewmodel.viewModelScope.launch {
                        viewmodel.createMenuItem(context)
                    }
                },
                context = context
            )
        }
    }


//    LazyColumn (
//        modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 8.dp)
//    ){
//        items(viewmodel.items) { item ->
//            MenuItemCard(
//                menuItem = item,
//                onEditClick = {}, //TODO
//                onDeleteClick = { viewmodel.deleteMenu(item) }
//            )
//        }
//    }

}