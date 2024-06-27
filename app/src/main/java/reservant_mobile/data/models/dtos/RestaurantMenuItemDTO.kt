package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantMenuItemDTO (
    val menuItemId: Int? = null,
    val restaurantId: Int? = null,
    val price: Double,
    val name: String,
    val alternateName: String? = null,
    val alcoholPercentage: Double?,
    val photoFileName: String? = null,
    val photo: String? = null,
    val stringPrice: String? = null,
    val stringAlcoholPercentage: String? = null
)