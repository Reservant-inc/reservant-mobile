package reservant_mobile.ui.navigation

import kotlinx.serialization.Serializable

data object RestaurantManagementRoutes {

    @Serializable
    object Restaurant

    @Serializable
    object RestaurantPreview

    @Serializable
    data class Stats(
        val statsType: Int,
        val queryId: Int = -1
    )

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

    @Serializable
    data class Edit(
        val restaurantId: Int
    )
}