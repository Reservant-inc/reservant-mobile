package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantDTO (
    val restaurantId: Int = Int.MIN_VALUE,
    val name: String,
    val restaurantType: String = "Restaurant",
    val nip: String = "",
    val address: String,
    val postalIndex: String = "",
    val city: String,
    val groupId: Int? = null,
    val groupName: String? = null,
    val rentalContract: String? = null,
    val alcoholLicense: String? = null,
    val businessPermission: String? = null,
    val idCard: String? = null,
    val tables:List<TableDTO> = emptyList(),
    val provideDelivery:Boolean = false,
    val logo:String? = null,
    val photos:List<String> = emptyList(),
    val isVerified:Boolean = false,
    val rating:Double? = null,
    val numberReviews:Int? = null,
    val distanceFrom: Double? = null,
    val description:String = "",
    val location: LocationDTO? = null,
    val maxReservationDurationMinutes: Int? = null,
    val reservationDeposit:Double? = null,
    val tags:List<String> = emptyList(),
    val openingHours: List<AvailableHours>? = null,
    val isArchived: Boolean? = null
){
    @Serializable
    data class AvailableHours(
        /***
         * Time in 'HH:mm:ss' format
         */
        val from: String? = null,
        /***
         * Time in 'HH:mm:ss' format
         */
        val until: String? = null
    )
}
