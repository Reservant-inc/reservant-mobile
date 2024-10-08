package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.PrefsKeys
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.services.LocalDataService
import reservant_mobile.data.services.UserService
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.UnderlinedItem
import reservant_mobile.ui.navigation.AuthRoutes
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.navigation.UserRoutes
import reservant_mobile.ui.viewmodels.LoginViewModel

@Composable
fun SettingsActivity(navController: NavController, themeChange: () -> Unit) {
    val loginViewModel = viewModel<LoginViewModel>()

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.padding(top = 8.dp))

            IconWithHeader(
                icon = Icons.Rounded.RestaurantMenu,
                text = stringResource(R.string.label_settings),
            )

            Spacer(modifier = Modifier.padding(top = 16.dp))

            if (Roles.RESTAURANT_EMPLOYEE !in UserService.UserObject.roles)
                UnderlinedItem(
                    icon = Icons.Filled.Person,
                    text = stringResource(id = R.string.label_my_profile),
                    onClick = {
                        navController.navigate(MainRoutes.UserProfile)
                    }
                )

            if (Roles.RESTAURANT_EMPLOYEE !in UserService.UserObject.roles)
                UnderlinedItem(
                    icon = Icons.Filled.AccountBalanceWallet,
                    text = stringResource(id = R.string.label_wallet),
                    onClick = { /* Navigate to Wallet */ }
                )

            if (Roles.RESTAURANT_EMPLOYEE !in UserService.UserObject.roles)
                UnderlinedItem(
                    icon = Icons.Filled.ShoppingCart,
                    text = stringResource(id = R.string.label_my_orders),
                    onClick = { navController.navigate(RestaurantRoutes.Ticket) }
                )

            UnderlinedItem(
                icon = Icons.AutoMirrored.Filled.Help,
                text = stringResource(id = R.string.label_helpdesk),
                onClick = { navController.navigate(RestaurantRoutes.TicketHistory) }
            )

            UnderlinedItem(
                icon = Icons.Filled.Info,
                text = stringResource(id = R.string.label_faq),
                onClick = { /* Navigate to FAQ */ }
            )

            if (Roles.RESTAURANT_EMPLOYEE !in UserService.UserObject.roles)
                UnderlinedItem(
                    icon = Icons.Filled.CardGiftcard,
                    text = stringResource(id = R.string.label_promo_codes),
                    onClick = { /* Navigate to Promo Codes */ }
                )

            UnderlinedItem(
                icon = Icons.Filled.Settings,
                text = stringResource(id = R.string.label_app_settings),
                onClick = { /* Navigate to App Settings */ }
            )

            if (Roles.RESTAURANT_EMPLOYEE !in UserService.UserObject.roles)
                UnderlinedItem(
                    icon = Icons.Filled.Delete,
                    text = stringResource(id = R.string.label_delete_account),
                    onClick = { navController.navigate(UserRoutes.ChatList) }
                )

            UnderlinedItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                text = stringResource(id = R.string.label_logout_action),
                onClick = {
                    loginViewModel.viewModelScope.launch {
                        if (Roles.RESTAURANT_EMPLOYEE in UserService.UserObject.roles) {
                            LocalDataService().saveData(
                                key = PrefsKeys.EMPLOYEE_CURRENT_RESTAURANT,
                                data = ""
                            )
                        }
                        loginViewModel.logout()
                        navController.navigate(AuthRoutes.Landing) {
                            popUpTo(0)
                        }
                    }
                }
            )

            UnderlinedItem(
                icon = Icons.Filled.Brightness4,
                text = "Temporary theme changer",
                onClick = { themeChange() }
            )
        }
    }
}