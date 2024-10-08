package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable


@Serializable
data class IngredientDTO (
    val ingredientId: Int? = null,
    val publicName: String? = null,
    val amountUsed: Double? = null,
    val unitOfMeasurement: UnitOfMeasurement? = null,
    val minimalAmount: Double? = null,
    val amountToOrder: Double? = null,
    val amount: Double? = null,
    val menuItem: IngredientMenuItemDTO? = null
){
    @Serializable
    data class IngredientMenuItemDTO(
        val menuItemId: Int,
        val amountUsed: Double
    )

}