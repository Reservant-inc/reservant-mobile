package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsDTO(
    val language: String? = null
)