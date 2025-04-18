package reservant_mobile.ui.navigation

import kotlinx.serialization.Serializable
import reservant_mobile.data.endpoints.Ingredients
import reservant_mobile.data.models.dtos.IngredientDTO

data object RestaurantRoutes {

    @Serializable
    object Map

    @Serializable
    data class Details(
        val restaurantId: Int
    )

    @Serializable
    data class AddOrder(
        val restaurantId: Int,
        val visitId: Int
    )

    @Serializable
    data class ManageOrders(
        val restaurantId: Int
    )

    @Serializable
    data class Warehouse(
        val restaurantId: Int
    )

    @Serializable
    data class Visit(
        val restaurantId: Int
    )

    @Serializable
    data class Reservation(
        val restaurantId: Int,
        val isReservation: Boolean
    )

    @Serializable
    object Order

    @Serializable
    data class Summary(
        val restaurantId: Int,
        val isReservation: Boolean
    )

    @Serializable
    data class Reviews(
        val restaurantId: Int
    )

    @Serializable
    data class AddReview(
        val restaurantId: Int
    )

    @Serializable
    data class EditReview(
        val reviewId: Int,
        val restaurantId: Int
    )

    @Serializable
    data class Tables(
        val restaurantId: Int
    )
    
    @Serializable
    data class OrderDetail(
        val visitId: Int
    )

    @Serializable
    data class Deliveries(
        val restaurantId: Int
    )

    @Serializable
    data class IngredientHistory(
        val ingredient: IngredientDTO
    )
}