package reservant_mobile.ui.navigation

import kotlinx.serialization.Serializable
import reservant_mobile.data.models.dtos.ReportDTO

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
    data class Ticket(
        val restaurantId: Int
    )

    @Serializable
    data class TicketHistory(
        val restaurantId: Int
    )

    @Serializable
    object Orders

    @Serializable
    data class VisitDetails(
        val visitId: Int
    )

    @Serializable
    data class ReportDetails(
        val report: ReportDTO,
    )

    @Serializable
    object FindFriends

    @Serializable
    object Wallet

    @Serializable
    object BecomeRestaurantOwner
}