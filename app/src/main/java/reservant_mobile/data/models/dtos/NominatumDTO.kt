package reservant_mobile.data.models.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable()
data class NominatumDTO(
    val lat: Double? = null,
    val lon: Double? = null,
    @SerialName("class")
    val className: String? = null,
    @SerialName("display_name")
    val displayName: String? = null
)