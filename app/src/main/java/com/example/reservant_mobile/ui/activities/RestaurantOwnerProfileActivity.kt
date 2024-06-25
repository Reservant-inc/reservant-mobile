package com.example.reservant_mobile.ui.activities

import com.example.reservant_mobile.ui.components.SettingItem
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.IconWithHeader
import com.example.reservant_mobile.ui.navigation.AuthRoutes
import com.example.reservant_mobile.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun RestaurantOwnerProfileActivity(navController: NavController, themeChange: () -> Unit) {
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

            SettingItem(
                icon = Icons.Filled.Person,
                text = stringResource(id = R.string.label_edit_profile),
                onClick = { /* Navigate to Edit Profile */ }
            )

            SettingItem(
                icon = Icons.Filled.AccountBalanceWallet,
                text = stringResource(id = R.string.label_wallet),
                onClick = { /* Navigate to Wallet */ }
            )

            SettingItem(
                icon = Icons.Filled.ShoppingCart,
                text = stringResource(id = R.string.label_my_orders),
                onClick = { /* Navigate to My Orders */ }
            )

            SettingItem(
                icon = Icons.AutoMirrored.Filled.Help,
                text = stringResource(id = R.string.label_helpdesk),
                onClick = { /* Navigate to Helpdesk */ }
            )

            SettingItem(
                icon = Icons.Filled.Info,
                text = stringResource(id = R.string.label_faq),
                onClick = { /* Navigate to FAQ */ }
            )

            SettingItem(
                icon = Icons.Filled.CardGiftcard,
                text = stringResource(id = R.string.label_promo_codes),
                onClick = { /* Navigate to Promo Codes */ }
            )

            SettingItem(
                icon = Icons.Filled.Settings,
                text = stringResource(id = R.string.label_app_settings),
                onClick = { /* Navigate to App Settings */ }
            )

            SettingItem(
                icon = Icons.Filled.Delete,
                text = stringResource(id = R.string.label_delete_account),
                onClick = { /* Navigate to Delete Account */ }
            )

            SettingItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                text = stringResource(id = R.string.label_logout_action),
                onClick = {
                    loginViewModel.viewModelScope.launch {
                        loginViewModel.logout()
                        navController.navigate(AuthRoutes.Landing) {
                            popUpTo(0)
                        }
                    }
                }
            )

            SettingItem(
                icon = Icons.Filled.Brightness4,
                text = "Temporary theme changer",
                onClick = { themeChange() }
            )
        }
    }
}