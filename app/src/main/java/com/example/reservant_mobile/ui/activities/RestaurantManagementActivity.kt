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
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RestaurantGroupDTO
import com.example.reservant_mobile.ui.components.IconWithHeader
import com.example.reservant_mobile.ui.components.OutLinedDropdownMenu
import com.example.reservant_mobile.ui.components.RestaurantInfoView
import com.example.reservant_mobile.ui.constants.RestaurantManagementRoutes
import com.example.reservant_mobile.ui.viewmodels.RestaurantManagementViewModel
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RestaurantManagementActivity() {

    val restaurantManageVM = viewModel<RestaurantManagementViewModel>()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RestaurantManagementRoutes.ACTIVITY_MANAGE) {
        composable(route = RestaurantManagementRoutes.ACTIVITY_MANAGE) {

            val groups = restaurantManageVM.groups
            var selectedGroup: RestaurantGroupDTO? by remember { mutableStateOf(null) }

            restaurantManageVM.viewModelScope.launch {
                restaurantManageVM.initialize()
            }

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
                    OutLinedDropdownMenu(
                        selectedOption = selectedGroup?.name ?: stringResource(R.string.label_management_choose_group),
                        itemsList = groups.map { it.name },
                        onOptionSelected = { name ->
                            selectedGroup = groups.find { it.name == name }
                            restaurantManageVM.viewModelScope.launch {
                                selectedGroup = selectedGroup?.let { group ->
                                    restaurantManageVM.getGroup(
                                        group.id
                                    )
                                }
                            }
                        },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                selectedGroup?.restaurants?.forEach { restaurant ->
                    RestaurantInfoView(
                        restaurant = restaurant,
                        onEditClick = { /*TODO*/ },
                        onManageEmployeeClick = { /*TODO*/ },
                        onManageMenuClick = { /*TODO*/ },
                        onManageSubscriptionClick = { /*TODO*/ }) {

                    }
                }
                Spacer(
                    modifier = Modifier.padding(bottom = 64.dp)
                )
            }
        }
    }
}