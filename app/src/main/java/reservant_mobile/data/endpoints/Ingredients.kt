package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/ingredients")
class Ingredients{
    @Resource("{ingredientId}")
    class IngredientId(val parent: Ingredients = Ingredients(), val ingredientId: String){
        @Resource("correct-amount")
        class CorrectAmount(val parent: IngredientId)
    }
}