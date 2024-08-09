package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class MoneyDTO(
    val transactionId: Int?,
    val title: String,
    val amount: Double,
    /***
     * Time in 'yyyy-MM-dd'T'HH:mm:ss.yyyy'Z'' format
     */
    val time: String?
)