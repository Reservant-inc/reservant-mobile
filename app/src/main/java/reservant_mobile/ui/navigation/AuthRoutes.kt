package reservant_mobile.ui.navigation

import kotlinx.serialization.Serializable

data object AuthRoutes{
    @Serializable
    object Landing

    @Serializable
    object Login

    @Serializable
    object Register
}