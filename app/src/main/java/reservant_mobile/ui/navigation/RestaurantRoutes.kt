package reservant_mobile.ui.navigation

import kotlinx.serialization.Serializable

data object RestaurantRoutes {

    @Serializable
    object Map

    @Serializable
    data class Details(
        val restaurantId: Int
    )

    @Serializable
    object Ticket

    @Serializable
    object Reservation

    @Serializable
    object TicketHistory

    @Serializable
    object Order

    @Serializable
    object Summary

    @Serializable
    data class AddReview(
        val restaurantId: Int
    )

    @Serializable
    data class EditReview(
        val reviewId: Int,
        val restaurantId: Int
    )
}