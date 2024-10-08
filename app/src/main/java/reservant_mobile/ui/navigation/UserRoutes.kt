package reservant_mobile.ui.navigation

import kotlinx.serialization.Serializable

data object UserRoutes {

    @Serializable
    object ChatList

    @Serializable
    data class Chat(
        val userName: String
    )

    @Serializable
    data class UserProfile(
        val userId: String
    )

    @Serializable
    object Ticket

    @Serializable
    object TicketHistory
}