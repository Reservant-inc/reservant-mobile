package com.example.reservant_mobile.data.utils

import android.provider.Settings.Global.getString
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.navigation.MainRoutes
import com.example.reservant_mobile.ui.navigation.RestaurantManagementRoutes


sealed class BottomNavItem(
    var route: Any?,
    var icon: ImageVector,
    var label: Int
) {
    data object Home :
        BottomNavItem(
            MainRoutes.Home,
            Icons.Filled.Home,
            R.string.label_home
        )

    data object Landing :
        BottomNavItem(
            null,
            Icons.Filled.Accessibility,
            R.string.label_TODO
        )

    data object Management :
        BottomNavItem(
            RestaurantManagementRoutes.Restaurant,
            Icons.Filled.Restaurant,
            R.string.label_management
        )

    data object Profile :
        BottomNavItem(
            MainRoutes.Profile,
            Icons.Filled.Settings,
            R.string.label_settings
        )

}