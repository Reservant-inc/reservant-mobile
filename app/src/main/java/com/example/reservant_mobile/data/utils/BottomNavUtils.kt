package com.example.reservant_mobile.data.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.reservant_mobile.ui.constants.MainRoutes

sealed class BottomNavItem(
    var route: String,
    var icon: ImageVector
) {
    data object Home :
        BottomNavItem(
            MainRoutes.ACTIVITY_HOME,
            Icons.Filled.Home
        )

    data object Landing :
        BottomNavItem(
            "",
            Icons.Filled.Accessibility
        )

    data object Login :
        BottomNavItem(
            "",
            Icons.Filled.AccountBox
        )

    data object Profile :
        BottomNavItem(
            MainRoutes.ACTIVITY_PROFILE,
            Icons.Filled.Settings
        )

}