package reservant_mobile.ui.activities

import OrdersActivity
import WalletActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.PermissionStrings
import reservant_mobile.data.constants.PrefsKeys
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.services.LocalDataService
import reservant_mobile.data.services.UserService
import reservant_mobile.ui.components.DeleteCountdownPopup
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.NotificationHandler
import reservant_mobile.ui.components.RequestPermission
import reservant_mobile.ui.components.UnderlinedItem
import reservant_mobile.ui.navigation.AuthRoutes
import reservant_mobile.ui.navigation.EventRoutes
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.RestaurantManagementRoutes
import reservant_mobile.ui.navigation.UserRoutes
import reservant_mobile.ui.viewmodels.LoginViewModel

@Composable
fun SettingsActivity(homeNavController: NavHostController, themeChange: () -> Unit, withBackButton:Boolean = false, onReturnClick: () -> Unit = {}) {
    val loginViewModel = viewModel<LoginViewModel>()
    var showLogoutPopup by remember { mutableStateOf(false) }
    var showDeleteAccountPopup by remember { mutableStateOf(false) }

    Surface {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = MainRoutes.Settings){
            composable<MainRoutes.Settings> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.padding(top = 8.dp))

                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_settings),
                        showBackButton = withBackButton,
                        onReturnClick = onReturnClick
                    )

                    Spacer(modifier = Modifier.padding(top = 16.dp))

                    if (Roles.RESTAURANT_EMPLOYEE !in UserService.UserObject.roles)
                        UnderlinedItem(
                            icon = Icons.Filled.Person,
                            text = stringResource(id = R.string.label_my_profile),
                            onClick = {
                                navController.navigate(UserRoutes.UserProfile(userId = UserService.UserObject.userId))
                            }
                        )

                    if (Roles.RESTAURANT_EMPLOYEE !in UserService.UserObject.roles)
                        UnderlinedItem(
                            icon = Icons.Filled.AccountBalanceWallet,
                            text = stringResource(id = R.string.label_wallet),
                            onClick = { navController.navigate(UserRoutes.Wallet) }
                        )

                    if (Roles.RESTAURANT_EMPLOYEE !in UserService.UserObject.roles)
                        UnderlinedItem(
                            icon = Icons.Filled.ShoppingCart,
                            text = stringResource(id = R.string.label_my_orders),
                            onClick = {
                                navController.navigate(UserRoutes.Orders)
                            }
                        )

                    UnderlinedItem(
                        icon = Icons.Filled.Report,
                        text = stringResource(id = R.string.label_complaints),
                        onClick = {
                            navController.navigate(UserRoutes.TicketHistory)
                        }
                    )


                    UnderlinedItem(
                        icon = Icons.AutoMirrored.Filled.Help,
                        text = stringResource(id = R.string.label_helpdesk),
                        onClick = {
                            navController.navigate(UserRoutes.Ticket)
                        }
                    )

//                    if (Roles.RESTAURANT_EMPLOYEE !in UserService.UserObject.roles)
//                        UnderlinedItem(
//                            icon = Icons.Filled.CardGiftcard,
//                            text = stringResource(id = R.string.label_promo_codes),
//                            onClick = {  Navigate to Promo Codes  }
//                        )
//
//
//                    UnderlinedItem(
//                        icon = Icons.Filled.Settings,
//                        text = stringResource(id = R.string.label_app_settings),
//                        onClick = { }
//                    )

                    UnderlinedItem(
                        icon = Icons.Filled.Brightness4,
                        text = stringResource(id = R.string.label_toggle_dark_theme),
                        onClick = { themeChange() },
                        actionIcon = null
                    )

                    if (Roles.RESTAURANT_EMPLOYEE !in UserService.UserObject.roles)
                        UnderlinedItem(
                            icon = Icons.Filled.Delete,
                            text = stringResource(id = R.string.label_delete_account),
                            onClick = {
                                showDeleteAccountPopup = true
                            },
                            actionIcon = null
                        )

                    UnderlinedItem(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        text = stringResource(id = R.string.label_logout_action),
                        onClick = {
                            showLogoutPopup = true
                        },
                        actionIcon = null
                    )
                }
                val logoutAction = {
                    loginViewModel.viewModelScope.launch {
                        if (Roles.RESTAURANT_EMPLOYEE in UserService.UserObject.roles) {
                            LocalDataService().saveData(
                                key = PrefsKeys.EMPLOYEE_CURRENT_RESTAURANT,
                                data = ""
                            )
                        }
                        loginViewModel.logout()
                        homeNavController.navigate(AuthRoutes.Landing) {
                            popUpTo(0)
                        }
                    }
                }

                if(showLogoutPopup)  {
                    DeleteCountdownPopup(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        countDownTimer = 0,
                        title = stringResource(R.string.label_logout_action),
                        text = stringResource(R.string.message_logout),
                        confirmText = stringResource(R.string.label_yes_capital),
                        dismissText = stringResource(R.string.label_cancel),
                        onDismissRequest = { showLogoutPopup = false },
                        onConfirm = {logoutAction()}
                    )
                }

                if(showDeleteAccountPopup)  {
                    DeleteCountdownPopup(
                        icon = Icons.Default.Delete,
                        title = stringResource(R.string.label_delete_account),
                        text = stringResource(R.string.message_delete_account),
                        confirmText = stringResource(R.string.label_yes_capital),
                        dismissText = stringResource(R.string.label_cancel),
                        onDismissRequest = { showDeleteAccountPopup = false },
                        onConfirm = {
                            loginViewModel.viewModelScope.launch {
                                loginViewModel.userService.deleteAccount()
                            }
                            logoutAction()
                        }
                    )
                }
            }
            composable<UserRoutes.UserProfile>{
                ProfileActivity(navController = navController, userId = it.toRoute<UserRoutes.UserProfile>().userId)
            }
            composable<UserRoutes.Orders>{
                OrdersActivity(navController = navController)
            }
            composable<UserRoutes.Ticket>{
                NewTicketActivity()
            }
            composable<UserRoutes.TicketHistory>{
                TicketHistoryActivity(navController = navController)
            }
            composable<UserRoutes.Wallet>{
                WalletActivity()
            }
            composable<EventRoutes.Details>{
                EventDetailActivity(
                    navController = navController,
                    eventId = it.toRoute<EventRoutes.Details>().eventId
                )
            }
            composable<UserRoutes.Chat>{
                ChatActivity(
                    navController = navController,
                    threadId = it.toRoute<UserRoutes.Chat>().threadId,
                    title = it.toRoute<UserRoutes.Chat>().threadTitle,
                )
            }

        }

    }
}