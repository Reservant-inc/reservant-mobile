package reservant_mobile.ui.activities

import android.annotation.SuppressLint
import android.app.NotificationManager
import androidx.activity.ComponentActivity.NOTIFICATION_SERVICE
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.PrefsKeys
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.constants.ThemePrefsKeys
import reservant_mobile.data.services.LocalDataService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.BottomNavItem
import reservant_mobile.ui.components.BottomNavigation
import reservant_mobile.ui.components.NotificationHandler
import reservant_mobile.ui.navigation.AuthRoutes
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.RegisterRestaurantRoutes
import reservant_mobile.ui.navigation.RestaurantManagementRoutes
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.theme.AppTheme
import reservant_mobile.ui.viewmodels.ReservantViewModel
import kotlin.coroutines.coroutineContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun HomeActivity() {
    val innerNavController = rememberNavController()
    val bottomBarState = remember { (mutableStateOf(true)) }
    val viewmodel = viewModel<ReservantViewModel>()
    val localDataService = LocalDataService()
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val notificationHandler by remember {
        mutableStateOf(NotificationHandler(context = context, primaryColor = primaryColor))
    }

    val isSystemInDarkMode = isSystemInDarkTheme()
    var darkTheme by remember {
        mutableStateOf(isSystemInDarkMode)
    }
    LaunchedEffect(key1 = Unit) {
        val tmp = localDataService.getData(PrefsKeys.APP_THEME)
        darkTheme = if(tmp.isEmpty()) isSystemInDarkMode else tmp == ThemePrefsKeys.DARK.themeValue
        notificationHandler.createSession()
        notificationHandler.awaitNotification()
    }

    val items = listOfNotNull(
        BottomNavItem.Home,
        BottomNavItem.Chats,
        BottomNavItem.Management.takeIf { Roles.RESTAURANT_OWNER in UserService.UserObject.roles },
        BottomNavItem.Profile
    )

    val selectedBottomBarItem = mutableStateOf<BottomNavItem?>(null)

    AppTheme (darkTheme = darkTheme) {
        Scaffold(
            bottomBar = {
                 BottomNavigation(
                     navController =  innerNavController,
                     bottomBarState = bottomBarState,
                     items = items,
                     defaultSelectedItem = selectedBottomBarItem
                 )
            }
        ){
            NavHost(navController = innerNavController, startDestination = MainRoutes.Home, modifier = Modifier.padding(it)){

                composable<MainRoutes.Home>{
                    LaunchedEffect(Unit) {
                        selectedBottomBarItem.value = items.first()
                    }
                    MapActivity(isUserLoggedIn = true)
                }
                composable<RestaurantManagementRoutes.Restaurant>{
                    RestaurantManagementActivity(navControllerHome = innerNavController)
                }
                composable<RegisterRestaurantRoutes.Register>{
                    RegisterRestaurantActivity(
                        onReturnClick = { innerNavController.popBackStack() },
                        navControllerHome = innerNavController
                    )
                }
                composable<MainRoutes.Settings>{
                    LaunchedEffect(Unit) {
                        selectedBottomBarItem.value = items.last()
                    }
                    SettingsActivity(
                        homeNavController = innerNavController,
                        themeChange = {
                            darkTheme = !darkTheme
                            viewmodel.viewModelScope.launch {
                                val tmp = if(darkTheme) ThemePrefsKeys.DARK else ThemePrefsKeys.LIGHT
                                localDataService.saveData(PrefsKeys.APP_THEME, tmp.themeValue)
                            }
                        },
                        closeWebsocketSession = { notificationHandler.close() }
                    )
                }
                composable<MainRoutes.ChatList> {
                    LaunchedEffect(Unit) {
                        selectedBottomBarItem.value = items[1]
                    }
                    ChatListActivity()
                }
                composable<AuthRoutes.Landing>{
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    LandingActivity()
                }

            }
        }
    }
}