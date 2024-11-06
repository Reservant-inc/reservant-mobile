package reservant_mobile.data.models.dtos

import com.example.reservant_mobile.R
import kotlinx.serialization.Serializable

@Serializable
data class EventDTO (
    val eventId: Int? = null,
    val name: String? = null,
    val createdAt: String? = null,
    val description: String,
    val maxPeople: Int? = null,
    val time: String,
    val photo: String? = null,
    val mustJoinUntil: String,
    val creator: Participant? = null,
    val restaurant: RestaurantDTO? = null,
    val restaurantId: Int? = null,
    val visitId: Int? = null,
    val participants: List<Participant>? = null,
    val distanceFrom: Double? = null,
    val numberInterested: Int? = null,
    val numberParticipants: Int? = null,
    val isArchived: Boolean? = null
){
    @Serializable
    data class Participant(
        val userId: String,
        val firstName: String,
        val lastName: String,
        val photo: String? = null
    )

    @Serializable
    enum class EventStatus(val stringId: Int){
        Future(R.string.label_event_status_future),
        NonJoinable(R.string.label_event_status_nonJoinable),
        Past(R.string.label_event_status_past)
    }
}