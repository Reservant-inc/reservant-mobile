package reservant_mobile.ui.activities

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import reservant_mobile.data.utils.BottomNavItem
import reservant_mobile.ui.components.BottomNavigation
import reservant_mobile.ui.navigation.EmployeeRoutes
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.theme.AppTheme

@Composable
fun EmployeeHomeActivity() {
    val innerNavController = rememberNavController()
    val bottomBarState = remember { (mutableStateOf(true)) }


    val isSystemInDarkMode = isSystemInDarkTheme()

    var darkTheme by remember {
        mutableStateOf(isSystemInDarkMode)
    }

    val items = listOfNotNull(
        BottomNavItem.Employee,
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
            NavHost(navController = innerNavController, startDestination = EmployeeRoutes.Home, modifier = Modifier.padding(it)){
                composable<EmployeeRoutes.Home>{
                    TestContent()
                }
                composable<MainRoutes.Profile>{
                    SettingsActivity(navController = innerNavController, themeChange = { darkTheme = !darkTheme } )
                }
            }
        }
    }
}

@Composable
fun TestContent(
) {
    Text("TEST TEXT")

}