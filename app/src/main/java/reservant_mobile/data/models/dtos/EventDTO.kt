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
    val creatorId: String? = null,
    val creatorFullName: String? = null,
    val restaurantId: Int,
    val restaurantName:String? = null,
    val visitId: Int? = null,
    val participants: List<Participants>? = null,
    val numberInterested: Int? = null
){
    @Serializable
    data class Participants(
        val userId: String,
        val firstName: String,
        val lastName: String,
        val photo: String? = null
    )
}