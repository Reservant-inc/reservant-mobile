package reservant_mobile.ui.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.utils.formatDateTime
import java.time.LocalDateTime

class EmployeeOrderViewModel(
    private val restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService()
) : ReservantViewModel() {

    val currentVisits: Flow<PagingData<VisitDTO>> = flow {
        val result = restaurantService.getVisits(
            restaurantId = restaurantId,
            dateStart = formatDateTime(LocalDateTime.now().toString(), "yyyy-MM-dd'T'HH:mm:ss\n"),
            dateEnd = null,
            orderBy = null
        )

        if (!result.isError && result.value != null) {
            emitAll(result.value.cachedIn(viewModelScope))
        } else {
            // Handle the error, e.g., log it or emit an empty PagingData
            emit(PagingData.empty())
            Log.e("ViewModel", "Error fetching current visits: ${result.errors}")
        }
    }.catch { exception ->
        emit(PagingData.empty())
        Log.e("ViewModel", "Exception in currentVisits flow: $exception")
    }

    // Similarly for pastVisits
    val pastVisits: Flow<PagingData<VisitDTO>> = flow {
        val result = restaurantService.getVisits(
            restaurantId = restaurantId,
            dateStart = null,
            dateEnd = formatDateTime(LocalDateTime.now().toString(), "yyyy-MM-dd'T'HH:mm:ss\n"),
            orderBy = null
        )

        if (!result.isError && result.value != null) {
            emitAll(result.value.cachedIn(viewModelScope))
        } else {
            emit(PagingData.empty())
            Log.e("ViewModel", "Error fetching past visits: ${result.errors}")
        }
    }.catch { exception ->
        emit(PagingData.empty())
        Log.e("ViewModel", "Exception in pastVisits flow: $exception")
    }
}

