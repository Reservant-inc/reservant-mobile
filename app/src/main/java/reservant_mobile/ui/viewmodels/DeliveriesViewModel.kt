package reservant_mobile.ui.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.data.services.IDeliveryService
import reservant_mobile.data.services.DeliveryService
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.utils.GetDeliveriesSort

class DeliveriesViewModel(
    private val restaurantId: Int,
    private val deliveryService: IDeliveryService = DeliveryService(),
    private val restaurantService: IRestaurantService = RestaurantService()
) : ReservantViewModel() {

    private val _deliveries = MutableStateFlow<Flow<PagingData<DeliveryDTO>>?>(null)
    val deliveries: StateFlow<Flow<PagingData<DeliveryDTO>>?> = _deliveries.asStateFlow()

    var isLoading = MutableStateFlow(false)
    var errorMessage = MutableStateFlow<String?>(null)

    var restaurantIngredients: List<IngredientDTO>? = null

    private var returnDelivered: Boolean = false

    init {
        viewModelScope.launch {
            fetchDeliveriesForRestaurant()
            getRestaurantIngredients()
        }
    }

    fun setReturnDelivered(isDelivered: Boolean) {
        returnDelivered = isDelivered
        viewModelScope.launch {
            fetchDeliveriesForRestaurant()
        }
    }

    suspend fun getDeliveryIngredients(deliveryId: Int): List<DeliveryDTO.DeliveryIngredientDTO>?{
        val result = deliveryService.getDelivery(deliveryId)

        if(!result.isError){
            return result.value?.ingredients
        }
        return null
    }

    suspend fun getRestaurantIngredients() {
        val result = restaurantService.getIngredients(restaurantId)

        if(!result.isError){
            restaurantIngredients = result.value
        }
    }

    private suspend fun fetchDeliveriesForRestaurant() {
        try {
            isLoading.value = true
            val result = restaurantService.getDeliveries(
                restaurantId = restaurantId,
                returnDelivered = returnDelivered,
                userId = null,
                userName = null,
                orderBy = GetDeliveriesSort.OrderTimeDesc
            )
            if (!result.isError) {
                _deliveries.value = result.value?.cachedIn(viewModelScope)
            } else {
                errorMessage.value = "Błąd: nie udało się pobrać listy dostaw."
            }
        } catch (ex: Exception) {
            errorMessage.value = "Wyjątek: ${ex.message}"
        } finally {
            isLoading.value = false
        }
    }


    fun confirmDelivered(deliveryId: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = null

                val result = deliveryService.confirmDelivery(deliveryId)
                if (result.isError) {
                    errorMessage.value = "Błąd: nie udało się potwierdzić dostawy."
                } else {
                    fetchDeliveriesForRestaurant()
                }
            } catch (ex: Exception) {
                errorMessage.value = "Wyjątek: ${ex.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun markCanceled(deliveryId: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = null

                val result = deliveryService.markDeliveryCanceled(deliveryId)
                if (result.isError) {
                    errorMessage.value = "Błąd: nie udało się anulować dostawy."
                } else {
                    fetchDeliveriesForRestaurant()
                }
            } catch (ex: Exception) {
                errorMessage.value = "Wyjątek: ${ex.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
