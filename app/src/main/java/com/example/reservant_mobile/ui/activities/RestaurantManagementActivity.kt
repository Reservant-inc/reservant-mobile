package com.example.reservant_mobile.ui.activities
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.ui.components.OutLinedDropdownMenu
import com.example.reservant_mobile.ui.components.RestaurantInfoView
import com.example.reservant_mobile.ui.viewmodels.RestaurantManagementViewModel
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RestaurantManagementActivity(navController: NavHostController) {
    val restaurantManageVM = viewModel<RestaurantManagementViewModel>()

    val restaurants = restaurantManageVM.restaurants
    var currentRestaurant by remember { mutableStateOf<RestaurantDTO?>(null) }
    var selectedRestaurant by remember { mutableStateOf<RestaurantDTO?>(null) }

    restaurantManageVM.viewModelScope.launch {
        restaurantManageVM.loadRestaurants()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.label_management_manage),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(all = 16.dp).fillMaxWidth()
        )
        if (restaurants != null) {
            OutLinedDropdownMenu(
                selectedOption = currentRestaurant?.name ?: stringResource(R.string.label_management_choose_restaurant),
                itemsList = restaurants.map { it.name },
                onOptionSelected = { name ->
                    selectedRestaurant = restaurants.find { it.name == name }
                    restaurantManageVM.viewModelScope.launch {
                        currentRestaurant =
                            selectedRestaurant?.let {
                                restaurantManageVM.getSingleRestaurant(it.id)
                            }
                    }
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        currentRestaurant?.let { restaurant ->
            RestaurantInfoView(restaurant,
                onEditClick = {

                },
                onManageEmployeeClick = {

                },
                onManageMenuClick = {

                },
                onManageSubscriptionClick = {

                },
                onDeleteClick = {
                restaurantManageVM.viewModelScope.launch {
                    restaurantManageVM.deleteSelectedRestaurant()
                }
            })
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewManage() {
    RestaurantManagementActivity(rememberNavController())
}