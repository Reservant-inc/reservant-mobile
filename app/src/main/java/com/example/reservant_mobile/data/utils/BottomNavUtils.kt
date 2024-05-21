package com.example.reservant_mobile.data.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.reservant_mobile.ui.navigation.MainRoutes
import com.example.reservant_mobile.ui.navigation.RestaurantManagementRoutes


sealed class BottomNavItem(
    var route: Any?,
    var icon: ImageVector
) {
    data object Home :
        BottomNavItem(
            MainRoutes.Home,
            Icons.Filled.Home
        )

    data object Landing :
        BottomNavItem(
            null,
            Icons.Filled.Accessibility
        )

    data object Management :
        BottomNavItem(
            RestaurantManagementRoutes.Restaurant,
            Icons.Filled.Restaurant
        )

    data object Profile :
        BottomNavItem(
            MainRoutes.Profile,
            Icons.Filled.Settings
        )

}