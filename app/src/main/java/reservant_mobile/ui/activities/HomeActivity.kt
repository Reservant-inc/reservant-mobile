package reservant_mobile.ui.activities

import OrdersActivity
import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import reservant_mobile.ui.components.BottomNavigation
import reservant_mobile.ui.navigation.AuthRoutes
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.RegisterRestaurantRoutes
import reservant_mobile.ui.navigation.RestaurantManagementRoutes
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.navigation.UserRoutes
import reservant_mobile.ui.theme.AppTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun HomeActivity() {
    val innerNavController = rememberNavController()
    val bottomBarState = remember { (mutableStateOf(true)) }


    val isSystemInDarkMode = isSystemInDarkTheme()

    var darkTheme by remember {
        mutableStateOf(isSystemInDarkMode)
    }

    AppTheme (darkTheme = darkTheme) {
        Scaffold(
            bottomBar = {
                 BottomNavigation(
                     navController =  innerNavController,
                     bottomBarState = bottomBarState
                 )
            }
        ){
            NavHost(navController = innerNavController, startDestination = MainRoutes.Home, modifier = Modifier.padding(it)){
                composable<MainRoutes.Home>{
                    MapActivity()
                }
                composable<RestaurantManagementRoutes.Restaurant>{
                    RestaurantManagementActivity(navControllerHome = innerNavController)
                }
                composable<RegisterRestaurantRoutes.Register>{
                    RegisterRestaurantActivity(navControllerHome = innerNavController)
                }
                composable<MainRoutes.Profile>{
                    SettingsActivity(navController = innerNavController, themeChange = { darkTheme = !darkTheme } )
                }
                composable<RestaurantRoutes.Details>{
                    RestaurantDetailActivity(navController = innerNavController, 1) //TODO: hardcoded id?
                }
                composable<RestaurantRoutes.Order>{
                    RestaurantOrderActivity(navController = innerNavController)
                }
                composable<AuthRoutes.Landing>{
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    LandingActivity()
                }
                composable<MainRoutes.Orders>{
                    OrdersActivity()
                }

                composable<UserRoutes.ChatList> {
                    ChatListActivity(navController = innerNavController)
                }
                composable<MainRoutes.UserProfile>{
                    ProfileActivity(navController = innerNavController)
                }
                composable<RestaurantRoutes.Ticket>{
                    NewTicketActivity()
                }

                composable<RestaurantRoutes.TicketHistory>{
                    TicketHistoryActivity(navController = innerNavController)
                }
            }
        }
    }
}