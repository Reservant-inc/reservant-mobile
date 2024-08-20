package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
class LoggedUserDTO (
    val userId: String? = null,
    val login: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val firstName: String,
    val lastName: String,
    val registeredAt: String? = null,
    val birthDate: String? = null,
    val roles: List<String>? = null,
    val employerId: String? = null,
    val photo: String? = null,
    val employments: List<RestaurantEmployeeDTO>? = null,
    val token: String? = null
)