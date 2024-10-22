package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class EventDTO (
    val eventId: Int? = null,
    val name: String? = null,
    val createdAt: String? = null,
    val description: String,
    val maxPeople: Int? = null,
    val time: String,
    val mustJoinUntil: String,
    val creator: Participant? = null,
    val creatorId: String? = null,
    val creatorFullName: String? = null,
    val restaurant: RestaurantDTO? = null,
    val restaurantId: Int? = null,
//    val restaurantName:String? = null,
    val visitId: Int? = null,
    val participants: List<Participant>? = null,
    val distance: Double? = null,
    val numberInterested: Int? = null,
    val numberParticipants: Int? = null
){
    @Serializable
    data class Participant(
        val userId: String,
        val firstName: String,
        val lastName: String,
        val photo: String? = null
    )

    @Serializable
    enum class EventStatus{
        Future,
        NonJoinable,
        Past
    }
}