package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class IngredientDTO (
    val ingredientId: Int? = null,
    val publicName: String? = null,
    val amountUsed: Double
)