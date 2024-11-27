package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUserDTO(
    val login : String,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val email: String,
    var phoneNumber: PhoneNumberDTO? = null,
    val password: String
)