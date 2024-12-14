package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PhoneNumberDTO(
    val code: String,
    val number: String
) {
    override fun toString(): String {
        return "$code$number"
    }
}