package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ReviewDTO(
    val reviewId: Int? = null,
    val restaurantId: Int? = null,
    val authorId: String? = null,
    val authorFullName: String? = null,
    val stars: Int,
    /***
     * Date in 'yyyy-MM-d'T'H:mm:ss.SSS'Z'' format
     */
    val createdAt: String? = null,
    val contents: String,
    /***
     * Date in 'yyyy-MM-d'T'H:mm:ss.SSS'Z'' format
     */
    val answeredAt: String? = null,
    val restaurantResponse: String? = null
)