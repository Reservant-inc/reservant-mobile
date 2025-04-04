package reservant_mobile.ui.activities

import VisitHistoryActivity
import WalletActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.data.models.dtos.ReportDTO
import reservant_mobile.data.services.LocalDataService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.toCustomNavType
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.DeleteCountdownPopup
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.NotificationHandler
import reservant_mobile.ui.components.RequestPermission
import reservant_mobile.ui.components.UnderlinedItem
import reservant_mobile.ui.navigation.AuthRoutes
import reservant_mobile.ui.navigation.EventRoutes
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.RegisterRestaurantRoutes
import reservant_mobile.ui.navigation.RestaurantManagementRoutes
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.navigation.UserRoutes
import reservant_mobile.ui.viewmodels.LoginViewModel
import kotlin.reflect.typeOf

@Composable
fun SettingsActivity(
    homeNavController: NavHostController,
    themeChange: () -> Unit,
    withBackButton:Boolean = false,
    onReturnClick: () -> Unit = {},
    restaurantId: Int = 0,
    closeWebsocketSession: suspend () -> Unit = {}
) {
    val loginViewModel = viewModel<LoginViewModel>()
    var showLogoutPopup by remember { mutableStateOf(false) }
    var showDeleteAccountPopup by remember { mutableStateOf(false) }

    Surface {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = MainRoutes.Settings(restaurantId = restaurantId)){
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
                            text = stringResource(id = R.string.label_my_visits),
                            onClick = {
                                navController.navigate(UserRoutes.Orders)
                            }
                        )

                    if (Roles.RESTAURANT_OWNER !in UserService.UserObject.roles && Roles.RESTAURANT_EMPLOYEE !in UserService.UserObject.roles)
                        UnderlinedItem(
                            icon = Icons.Filled.RestaurantMenu,
                            text = stringResource(id = R.string.label_become_restaurant_owner),
                            onClick = {
                                navController.navigate(UserRoutes.BecomeRestaurantOwner)
                            }
                        )

                    UnderlinedItem(
                        icon = Icons.Filled.Report,
                        text = stringResource(id = R.string.label_complaints),
                        onClick = {
                            navController.navigate(UserRoutes.TicketHistory(restaurantId = restaurantId))
                        }
                    )


                    UnderlinedItem(
                        icon = Icons.AutoMirrored.Filled.Help,
                        text = stringResource(id = R.string.label_helpdesk),
                        onClick = {
                            navController.navigate(UserRoutes.Ticket(restaurantId = restaurantId))
                        }
                    )

                    UnderlinedItem(
                        icon = Icons.Filled.Security,
                        text = stringResource(id = R.string.label_privacy),
                        onClick = {
                            navController.navigate(UserRoutes.PrivacyPolicy)
                        }
                    )

                    UnderlinedItem(
                        icon = Icons.Filled.ContactPage,
                        text = stringResource(id = R.string.label_terms),
                        onClick = {
                            navController.navigate(UserRoutes.TermsOfService)
                        }
                    )

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
                        closeWebsocketSession()
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
                VisitHistoryActivity(navController = navController)
            }
            composable<UserRoutes.Ticket>{
                NewTicketActivity(navController = navController, restaurantId = it.toRoute<UserRoutes.Ticket>().restaurantId,)
            }
            composable<UserRoutes.TicketHistory>{
                TicketHistoryActivity(navController = navController, restaurantId = it.toRoute<UserRoutes.TicketHistory>().restaurantId)
            }
            composable<UserRoutes.Wallet>{
                WalletActivity(
                    onReturnClick = { navController.popBackStack() }
                )
            }
            composable<UserRoutes.PrivacyPolicy>{
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {}

                val uriHandler = LocalUriHandler.current
                LaunchedEffect(key1 = false) {
                    uriHandler.openUri("https://reservant.app/privacy-policy")
                    navController.popBackStack()
                }
            }
            composable<UserRoutes.TermsOfService>{
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {}

                val uriHandler = LocalUriHandler.current
                LaunchedEffect(key1 = false) {
                    uriHandler.openUri("https://reservant.app/terms-of-service")
                    navController.popBackStack()
                }
            }
            composable<UserRoutes.ReportDetails>(
                typeMap = mapOf(typeOf<ReportDTO>() to toCustomNavType(ReportDTO.serializer())),
            ) {
                val item = it.toRoute<UserRoutes.ReportDetails>().report
                ReportDetailsActivity(report = item, navController = navController)
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
            composable<UserRoutes.BecomeRestaurantOwner>{
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    IconWithHeader(
                        icon = Icons.Filled.RestaurantMenu,
                        text = stringResource(id = R.string.label_become_restaurant_owner),
                        showBackButton = true,
                        onReturnClick = {navController.popBackStack()})
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = stringResource(id = R.string.art_header_become_restaurant_owner),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(id = R.string.art_body1_become_restaurant_owner),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Justify,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.people_restaurant),
                                contentDescription = "Restaurant people",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = stringResource(id = R.string.art_body2_become_restaurant_owner),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Justify,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.become_restaurant_owner),
                                contentDescription = "Become restaurant owner",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = stringResource(id = R.string.art_body3_become_restaurant_owner),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Justify,
                            color = MaterialTheme.colorScheme.onBackground
                        )


                        Spacer(modifier = Modifier.height(24.dp))

                        ButtonComponent(
                            icon = Icons.Filled.Restaurant,
                            label = stringResource(id = R.string.label_register_restaurant),
                            onClick = { homeNavController.navigate(RegisterRestaurantRoutes.Register)}
                        )
                        
                    }
                }
            }

            composable<UserRoutes.VisitDetails>{
                VisitDetailActivity(
                    visitId = it.toRoute<UserRoutes.VisitDetails>().visitId,
                    onReturnClick = {navController.popBackStack()}
                )
            }
            composable<RestaurantRoutes.Details> {
                RestaurantDetailActivity(
                    restaurantId = it.toRoute<RestaurantRoutes.Details>().restaurantId,
                    onReturnClick = { navController.popBackStack() }
                )
            }

        }

    }
}