package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.TableDTO
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.RestaurantService

class TablesViewModel(private val restaurantId: Int) : ViewModel() {

    private val restaurantService: IRestaurantService = RestaurantService()

    var selectedTable: TableDTO? by mutableStateOf(null)
    var isEditSelected by mutableStateOf(false)
    var isAddSelected by mutableStateOf(false)

    var numberOfPeople: Int? by mutableStateOf(null)

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

    suspend fun updateTables(): Boolean {
        val result = restaurantService.putTables(restaurantId, tables = tables.value)
        if (!result.isError){
            fetchTables()
        }
        return result.isError
    }

    fun removeTable(tableId: Int) {
        _tables.value = tables.value.filter {
            it.tableId != tableId
        }
    }

    fun addTable(tableId: Int) {
        numberOfPeople?.let {
            _tables.value = listOf(
                *tables.value.toTypedArray(),
                TableDTO(tableId = tableId, capacity = it)
            )
        }

    }

    fun generateTableId(): Int {

        return if (tables.value.isEmpty()){
            1
        } else {
            tables.value.maxBy {
                it.tableId
            }.tableId + 1
        }
    }


}
