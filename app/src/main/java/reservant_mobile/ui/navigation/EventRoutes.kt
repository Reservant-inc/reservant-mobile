package reservant_mobile.ui.navigation

import kotlinx.serialization.Serializable

data object EventRoutes{
    @Serializable
    data class Details(
        val eventId: Int
    )

}