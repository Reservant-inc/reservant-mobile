package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
class UserDTO (
    val userId: String? = null,
    val login: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val firstName: String,
    val lastName: String,
    /***
     * Date in 'yyyy-MM-dd'T'HH:mm:ss.SSS'Z'' format
     */
    val registeredAt: String? = null,
    /***
     * Date in 'yyyy-MM-dd' format
     */
    val birthDate: String? = null,
    val roles: List<String>? = null,
    val employments: List<RestaurantEmployeeDTO>? = null,
    val employerId: String? = null,
    val token: String? = null,
    val photo: String? = null,
    val friendStatus: FriendStatus? = null
)