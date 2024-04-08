package com.example.reservant_mobile.data.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    var title: String,
    var icon: ImageVector
) {
    data object Home :
        BottomNavItem(
            "Home",
            Icons.Filled.Home
        )

    data object Landing :
        BottomNavItem(
            "Landing",
            Icons.Filled.Accessibility
        )

    data object Login :
        BottomNavItem(
            "Login",
            Icons.Filled.AccountBox
        )

    data object Register :
        BottomNavItem(
            "Register",
            Icons.Filled.AccountTree
        )
}