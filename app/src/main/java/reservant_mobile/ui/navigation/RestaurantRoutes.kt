package reservant_mobile.ui.navigation

import kotlinx.serialization.Serializable

data object RestaurantRoutes {

    @Serializable
    object Map

    @Serializable
    data class Details(
        val restaurantId: Int
    )
}