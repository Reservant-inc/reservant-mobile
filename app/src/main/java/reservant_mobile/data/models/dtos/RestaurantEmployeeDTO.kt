package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantEmployeeDTO(
    val userId: String? = null,
    val login: String? = null,
    val restaurant: RestaurantDTO? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val registeredAt: String? = null,
    val birthDate: String? = null,
    val phoneNumber: String? = null,
    val employments: List<RestaurantEmployeeDTO>? = null,
    val password: String? = null,
    val roles: List<String>? = null,
    val photo: String? = null,
    val friendStatus: FriendStatus? = null,
    val employerId: String? = null,
    val restaurantName: String? = null,
    val isHallEmployee:Boolean = false,
    val isBackdoorEmployee:Boolean = false,
    val dateFrom: String? = null,
    val dateUntil: String? = null,
    val employmentId: Int? = null,
    val employeeId: String? = null
)