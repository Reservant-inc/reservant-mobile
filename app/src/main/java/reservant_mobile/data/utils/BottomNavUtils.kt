package reservant_mobile.data.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.reservant_mobile.R
import reservant_mobile.ui.navigation.EmployeeRoutes
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.RestaurantManagementRoutes


sealed class BottomNavItem(
    var route: Any,
    var icon: ImageVector,
    var label: Int
) {
    data object Home :
        BottomNavItem(
            MainRoutes.Home,
            Icons.Filled.Home,
            R.string.label_home
        )

    data object Chats :
        BottomNavItem(
            MainRoutes.ChatList,
            Icons.Filled.PersonPin,
            R.string.label_chats
        )

    data object Management :
        BottomNavItem(
            RestaurantManagementRoutes.Restaurant,
            Icons.Filled.Restaurant,
            R.string.label_management
        )

    data object Profile :
        BottomNavItem(
            MainRoutes.Settings,
            Icons.Filled.Settings,
            R.string.label_settings
        )

    data object Employee :
        BottomNavItem(
            EmployeeRoutes.Home,
            Icons.Filled.RestaurantMenu,
            R.string.label_employee
        )

}