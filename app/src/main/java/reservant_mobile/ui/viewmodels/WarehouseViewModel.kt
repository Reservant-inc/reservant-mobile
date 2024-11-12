import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.DeliveryService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.utils.GetIngredientsSort
import reservant_mobile.ui.viewmodels.ReservantViewModel

class WarehouseViewModel(
    private val restaurantService: RestaurantService = RestaurantService(),
    private val deliveryService: DeliveryService = DeliveryService()
) : ReservantViewModel() {

    private val _ingredients = MutableStateFlow<List<IngredientDTO>>(emptyList())
    val ingredients: StateFlow<List<IngredientDTO>> = _ingredients.asStateFlow()

    var isAddDeliveryDialogVisible by mutableStateOf(false)
    var selectedIngredient: IngredientDTO? = null

    fun getIngredientsFlow(
        restaurantId: Int,
        orderBy: GetIngredientsSort? = null
    ): Flow<PagingData<IngredientDTO>> = flow {
        val result = restaurantService.getIngredients(
            restaurantId = restaurantId,
            orderBy = orderBy
        )
        if (!result.isError && result.value != null) {
            emitAll(result.value.cachedIn(viewModelScope))
        } else {
            emit(PagingData.empty())
        }
    }.catch {
        emit(PagingData.empty())
    }


    fun showAddDeliveryDialog(ingredient: IngredientDTO) {
        selectedIngredient = ingredient
        isAddDeliveryDialogVisible = true
    }

    fun addDelivery(restaurantId: Int, storeName: String, amountOrdered: Int) {
        selectedIngredient?.let { ingredient ->
            viewModelScope.launch {
                val delivery = DeliveryDTO(
                    restaurantId = restaurantId,
                    ingredients = listOf(
                        DeliveryDTO.DeliveryIngredientDTO(
                            ingredientId = ingredient.ingredientId,
                            amountOrdered = amountOrdered.toDouble(),
                            storeName = storeName
                        )
                    )
                )
                val result = deliveryService.addDelivery(delivery)
                if (!result.isError) {
                    // Handle successful delivery addition, e.g., refresh data or show a success message
                } else {
                    // Handle error, e.g., show an error message
                }
            }
        }
    }
}
