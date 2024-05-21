package com.example.reservant_mobile.ui.activities

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RestaurantGroupDTO
import com.example.reservant_mobile.ui.components.IconWithHeader
import com.example.reservant_mobile.ui.components.OutLinedDropdownMenu
import com.example.reservant_mobile.ui.components.RestaurantInfoView
import com.example.reservant_mobile.ui.navigation.RestaurantManagementRoutes
import com.example.reservant_mobile.ui.viewmodels.RestaurantManagementViewModel
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RestaurantManagementActivity() {

    val restaurantManageVM = viewModel<RestaurantManagementViewModel>()
    val navController = rememberNavController()

    val groups = restaurantManageVM.groups
    var selectedGroup: RestaurantGroupDTO? by remember { mutableStateOf(null) }

    restaurantManageVM.viewModelScope.launch {
        restaurantManageVM.initialize()
    }

    NavHost(navController = navController, startDestination = RestaurantManagementRoutes.Restaurant){
        composable<RestaurantManagementRoutes.Restaurant>{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 8.dp, 16.dp, 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                IconWithHeader(
                    icon = Icons.Rounded.RestaurantMenu,
                    text = stringResource(R.string.label_management_manage),
                    scale = 0.9F
                )

                if (groups != null) {
                    // Displaying multiple groups
                    if(groups.size > 1){
                        OutLinedDropdownMenu(
                            label = stringResource(R.string.label_group),
                            selectedOption = selectedGroup?.name ?: stringResource(R.string.label_management_choose_group),
                            itemsList = groups.map { it.name },
                            onOptionSelected = { name ->
                                selectedGroup = groups.find { it.name == name }
                                restaurantManageVM.viewModelScope.launch {
                                    selectedGroup = selectedGroup?.let { group ->
                                        group.id?.let { it1 ->
                                            restaurantManageVM.getGroup(
                                                it1
                                            )
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                        )
                        // Displaying single group
                    }else if(groups.size == 1){
                        restaurantManageVM.viewModelScope.launch {
                            selectedGroup = groups[0].id?.let { it1 ->
                                restaurantManageVM.getGroup(
                                    it1
                                )
                            }
                        }
                    }else{
                        Text(
                            text = "You have no restaurants :("
                        )
                    }
                }

                selectedGroup?.restaurants?.forEach { restaurant ->
                    RestaurantInfoView(
                        restaurant = restaurant,
                        onEditClick = { /*TODO*/ },
                        onManageEmployeeClick = { navController.navigate(
                            RestaurantManagementRoutes.Employee(restaurantId = restaurant.id)
                        ) },
                        onManageMenuClick = { navController.navigate(
                            RestaurantManagementRoutes.Menu(restaurantId = restaurant.id)
                        ) },
                        onManageSubscriptionClick = { /*TODO*/ },
                        onDeleteClick = {
                            restaurantManageVM.viewModelScope.launch {
                                // TODO: restaurantManageVM.deleteRestaurant(restaurant.id)
                            }
                        }
                    )
                }
                Spacer(
                    modifier = Modifier.padding(bottom = 64.dp)
                )
            }
        }
        composable<RestaurantManagementRoutes.Menu>{
            MenuManagementActivity(
                restaurantId = it.toRoute<RestaurantManagementRoutes.Menu>().restaurantId
            )
        }

        composable<RestaurantManagementRoutes.Employee>{
            EmployeeManagementActivity(
                restaurantId = it.toRoute<RestaurantManagementRoutes.Employee>().restaurantId
            )
        }
    }


}