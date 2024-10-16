package reservant_mobile.ui.activities

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.BottomNavItem
import reservant_mobile.ui.components.BottomNavigation
import reservant_mobile.ui.navigation.AuthRoutes
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.RegisterRestaurantRoutes
import reservant_mobile.ui.navigation.RestaurantManagementRoutes
import reservant_mobile.ui.navigation.RestaurantRoutes
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

    val items = listOfNotNull(
        BottomNavItem.Home,
        BottomNavItem.Social,
        BottomNavItem.Management.takeIf { Roles.RESTAURANT_OWNER in UserService.UserObject.roles },
        BottomNavItem.Profile
    )

    AppTheme (darkTheme = darkTheme) {
        Scaffold(
            bottomBar = {
                 BottomNavigation(
                     navController =  innerNavController,
                     bottomBarState = bottomBarState,
                     items = items
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
                composable<MainRoutes.Settings>{
                    SettingsActivity(homeNavController = innerNavController, themeChange = { darkTheme = !darkTheme } )
                }
                composable<RestaurantRoutes.Reservation>{
                    RestaurantReservationActivity(navController = innerNavController)
                }
                composable<MainRoutes.Social> { 
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