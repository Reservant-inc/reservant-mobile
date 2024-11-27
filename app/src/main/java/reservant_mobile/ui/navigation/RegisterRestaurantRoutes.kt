package reservant_mobile.ui.navigation

import kotlinx.serialization.Serializable

data object RegisterRestaurantRoutes{
    @Serializable
    object Register

    @Serializable
    object Inputs

    @Serializable
    object Files

    @Serializable
    object OpeningHours

    @Serializable
    object Description
}