package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.constants.Roles
import com.example.reservant_mobile.data.services.UserService
import com.example.reservant_mobile.ui.components.ButtonComponent
import com.example.reservant_mobile.ui.navigation.AuthRoutes
import com.example.reservant_mobile.ui.navigation.MainRoutes
import com.example.reservant_mobile.ui.navigation.RegisterRestaurantRoutes
import com.example.reservant_mobile.ui.navigation.RestaurantRoutes
import com.example.reservant_mobile.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun RestaurantOwnerProfileActivity(navController: NavController, darkTheme: MutableState<Boolean>){

    val loginViewModel = viewModel<LoginViewModel>()

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var newRestaurantLabel:Int = R.string.label_become_restaurant_owner
            if(Roles.RESTAURANT_OWNER in UserService.User.roles)
                newRestaurantLabel = R.string.label_register_restaurant

            ButtonComponent(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),
                label = stringResource(id = newRestaurantLabel),
                onClick = {
                    navController.navigate(RegisterRestaurantRoutes.Register)
                },
            )

            ButtonComponent(
                label = "Temporary theme changer",
                onClick = { darkTheme.value = !darkTheme.value }
            )

            ButtonComponent(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),
                label = "Restaurant Detail Preview",
                onClick = {
                    navController.navigate(RestaurantRoutes.Details(restaurantId = 1))
                },
            )

            ButtonComponent(
                label = stringResource(id = R.string.lable_logout_action),
                onClick = {
                    loginViewModel.viewModelScope.launch{
                        loginViewModel.logout()
                        navController.navigate(AuthRoutes.Landing){
                            popUpTo(MainRoutes.Home){
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }
    }
}