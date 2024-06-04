package com.example.reservant_mobile.ui.navigation

import kotlinx.serialization.Serializable

object RestaurantRoutes {

    @Serializable
    object Map

    @Serializable
    data class Details(
        val restaurantId: Int
    )
}