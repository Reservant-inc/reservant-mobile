package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantEmployeeDTO(
    val userId: String = "",
    val restaurantId: String = "",
    val login: String = "",
    val email: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val registeredAt: String? = null,
    val birthDate: String? = null,
    val phoneNumber: String = "",
    val employments: List<RestaurantEmployeeDTO> = emptyList(),
    val password: String = "",
    val roles: List<String> = emptyList(),
    val employerId: String? = null,
    val isHallEmployee:Boolean = false,
    val isBackdoorEmployee:Boolean = false,
    val dateFrom: String = "",
    val dateUntil: String? = null,
    val employmentId: Int? = null,
    val employeeId: String = userId
)