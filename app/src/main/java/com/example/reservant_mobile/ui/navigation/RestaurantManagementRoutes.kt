package com.example.reservant_mobile.ui.navigation

import kotlinx.serialization.Serializable

data object RestaurantManagementRoutes {

    @Serializable
    object Restaurant

    @Serializable
    data class Menu(
        val restaurantId: Int
    )

    @Serializable
    data class MenuItem(
        val menuId: Int,
        val restaurantId: Int
    )

    @Serializable
    data class Employee(
        val restaurantId: Int
    )
}