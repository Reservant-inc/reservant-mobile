package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
class ErrorResponseDTO (
    val status: Int,
    val errors: Map<String, List<String>>,
    val errorCodes: Map<String, List<String>>
)