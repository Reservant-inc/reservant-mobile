package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/ingredients")
class Ingredients{
    @Resource("{ingredientId}")
    class IngredientId(val parent: Ingredients = Ingredients(), val ingredientId: String){
        @Resource("correct-amount")
        class CorrectAmount(val parent: IngredientId)
        @Resource("history")
        class History(
            val parent: IngredientId,
            val dateFrom: String? = null,
            val dateUntil: String? = null,
            val userId: String? = null,
            val comment: String? = null,
            val page: Int? = null,
            val perPage: Int? = null
        )
    }
}