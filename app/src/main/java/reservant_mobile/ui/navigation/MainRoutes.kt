package reservant_mobile.ui.navigation

import kotlinx.serialization.Serializable

data object MainRoutes{
    @Serializable
    object Home

    @Serializable
    data class Settings(
        val restaurantId: Int = 0
    )

    @Serializable
    object ChatList

}

