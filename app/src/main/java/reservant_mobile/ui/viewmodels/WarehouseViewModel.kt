import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.data.models.dtos.IngredientDTO
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

    var isAddIngredientDialogVisible by mutableStateOf(false)
    var isCartVisible by mutableStateOf(false)
    val cart = mutableStateListOf<DeliveryDTO.DeliveryIngredientDTO>()

    var ingredientsWithoutAmountToOrderList by mutableStateOf<List<IngredientDTO>>(emptyList())
    var showMissingAmountToOrderDialog by mutableStateOf(false)

    var showAddedToCartMessage by mutableStateOf(false)
    var addedToCartMessage by mutableStateOf("")

    fun loadIngredients(
        restaurantId: Int,
        orderBy: GetIngredientsSort? = null
    ) {
        viewModelScope.launch {
            val result = restaurantService.getIngredients(
                restaurantId = restaurantId,
                orderBy = orderBy
            )
            if (!result.isError && result.value != null) {
                _ingredients.value = result.value
            } else {
                _ingredients.value = emptyList()
            }
        }
    }

    fun showAddDeliveryDialog(ingredient: IngredientDTO) {
        selectedIngredient = ingredient
        isAddDeliveryDialogVisible = true
    }

    fun addToCart(item: DeliveryDTO.DeliveryIngredientDTO) {
        val alreadyInCart = cart.any { it.ingredientId == item.ingredientId }
        if (!alreadyInCart) {
            cart.add(item)
        }
    }

    fun removeFromCart(item: DeliveryDTO.DeliveryIngredientDTO) {
        cart.remove(item)
    }

    fun submitOrder(restaurantId: Int) {
        viewModelScope.launch {
            val delivery = DeliveryDTO(
                restaurantId = restaurantId,
                ingredients = cart.toList()
            )
            val result = deliveryService.addDelivery(delivery)
            if (!result.isError) {
                cart.clear()
                isCartVisible = false
                loadIngredients(restaurantId)
            } else {
                // Obsługa błędu
            }
        }
    }

    fun generateNewList() {
        val ingredientsToAdd = ingredients.value.filter { ingredient ->
            val quantity = ingredient.amount ?: 0.0
            val minQuantity = ingredient.minimalAmount ?: 0.0
            val amountToOrder = ingredient.amountToOrder
            val isAlreadyInCart = cart.any { it.ingredientId == ingredient.ingredientId }
            quantity < minQuantity && amountToOrder != null && !isAlreadyInCart
        }

        cart.addAll(ingredientsToAdd.map { ingredient ->
            DeliveryDTO.DeliveryIngredientDTO(
                ingredientId = ingredient.ingredientId ?: 0,
                amountOrdered = ingredient.amountToOrder ?: 0.0,
                storeName = null
            )
        })

        if (ingredientsToAdd.isNotEmpty()) {
            val addedCount = ingredientsToAdd.size
            addedToCartMessage = "Pomyślnie dodano $addedCount składników do koszyka."
            showAddedToCartMessage = true
        }

        val ingredientsWithoutAmountToOrder = ingredients.value.filter { ingredient ->
            val quantity = ingredient.amount ?: 0.0
            val minQuantity = ingredient.minimalAmount ?: 0.0
            val amountToOrder = ingredient.amountToOrder
            val isAlreadyInCart = cart.any { it.ingredientId == ingredient.ingredientId }
            quantity < minQuantity && amountToOrder == null && !isAlreadyInCart
        }
        if (ingredientsWithoutAmountToOrder.isNotEmpty()) {
            ingredientsWithoutAmountToOrderList = ingredientsWithoutAmountToOrder
            showMissingAmountToOrderDialog = true
        }
    }

    fun addIngredient(ingredient: IngredientDTO) {
        viewModelScope.launch {
            val result = restaurantService.addIngredient(ingredient)
            if (!result.isError && result.value != null) {
                loadIngredients(ingredient.restaurantId ?: 0)
                isAddIngredientDialogVisible = false
            } else {
                // Obsługa błędu
            }
        }
    }
}
