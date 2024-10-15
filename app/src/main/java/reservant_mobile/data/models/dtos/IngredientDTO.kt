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

    @Serializable
    data class CorrectionDTO(
        val correctionId: Int? = null,
        val ingredient: IngredientDTO? = null,
        val oldAmount: Double? = null,
        val newAmount: Double,
        /***
         * Date in 'yyyy-MM-d'T'H:mm:ss.SSS'Z'' format
         */
        val correctionDate: String? = null,
        /***
         * userId, firstName, lastName, photo
         */
        val user:UserSummaryDTO? = null,
        val comment: String? = null
    )

}