package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDTO (
    val deliveryId: Int? = null,
    val id: Int? = null,
    /***
     * Time in 'yyyy-MM-dd'T'HH:mm:ss.SSS'Z'' format
     */
    val orderTime: String? = null,
    /***
     * Time in 'yyyy-MM-dd'T'HH:mm:ss.SSS'Z'' format
     */
    val deliveredTime: String? = null,
    val restaurantId: Int? = null,
    val userId: String? = null,
    val userFullName: String? = null,
    val cost: Double? = null,
    val ingredients: List<DeliveryIngredientDTO>? = null
){
    @Serializable
    data class DeliveryIngredientDTO(
        val deliveryId: Int? = null,
        val ingredientId: Int? = null,
        val amountOrdered: Double? = null,
        val amountDelivered: Double? = null,
        /***
         * Time in 'yyyy-MM-dd'T'HH:mm:ss.SSS'Z'' format
         */
        val expiryDate: String? = null,
        val storeName: String? = null,
        val ingredientName: String? = null
    )
}