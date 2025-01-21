package reservant_mobile.ui.navigation

import kotlinx.serialization.Serializable

data object UserRoutes {

    @Serializable
    object ChatList

    @Serializable
    data class Chat(
        val threadId: Int,
        val threadTitle: String
    )

    @Serializable
    data class UserProfile(
        val userId: String
    )

    @Serializable
    object Ticket

    @Serializable
    object TicketHistory

    @Serializable
    object Orders

    @Serializable
    data class VisitDetails(
        val visitId: Int
    )

    @Serializable
    object FindFriends

    @Serializable
    object Wallet
}