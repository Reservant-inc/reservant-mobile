package reservant_mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.TableDTO
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.RestaurantService

class TablesViewModel(private val restaurantId: Int) : ViewModel() {

    private val restaurantService: IRestaurantService = RestaurantService()

    private val _tables = MutableStateFlow<List<TableDTO>>(emptyList())
    val tables: StateFlow<List<TableDTO>> get() = _tables

    init {
        fetchTables()
    }

    private fun fetchTables() {
        viewModelScope.launch {
            val result = restaurantService.getRestaurant(restaurantId)
            if (!result.isError) {
                _tables.value = result.value?.tables ?: emptyList()
            }
        }
    }
}
