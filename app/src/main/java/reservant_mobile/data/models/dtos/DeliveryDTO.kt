package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDTO (
    val delieryId: Int? = null,
    val orderTime: String? = null,
    val deliveredTime: String? = null,
    val userId: String? = null,
    val userFullName: String? = null,
    val cost: Double? = null
)