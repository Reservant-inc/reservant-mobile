package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable


@Serializable
data class IngredientDTO (
    val ingredientId: Int? = null,
    val publicName: String? = null,
    val amountUsed: Double,
    val unitOfMeasurement: UnitOfMeasurement? = null,
    val minimalAmount: Double? = null,
    val amountToOrder: Double? = null,
    val amount: Double? = null
)