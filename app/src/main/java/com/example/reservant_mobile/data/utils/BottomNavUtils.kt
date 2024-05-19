package com.example.reservant_mobile.data.utils

import android.provider.Settings.Global.getString
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.constants.MainRoutes
import com.example.reservant_mobile.ui.constants.RestaurantManagementRoutes

sealed class BottomNavItem(
    var route: String,
    var icon: ImageVector,
    var label: Int
) {
    data object Home :
        BottomNavItem(
            MainRoutes.ACTIVITY_HOME,
            Icons.Filled.Home,
            R.string.label_home
        )

    data object Landing :
        BottomNavItem(
            "",
            Icons.Filled.Accessibility,
            R.string.label_TODO
        )

    data object Management :
        BottomNavItem(
            RestaurantManagementRoutes.ACTIVITY_MANAGE,
            Icons.Filled.Restaurant,
            R.string.label_management
        )

    data object Profile :
        BottomNavItem(
            MainRoutes.ACTIVITY_PROFILE,
            Icons.Filled.Settings,
            R.string.label_settings
        )

}