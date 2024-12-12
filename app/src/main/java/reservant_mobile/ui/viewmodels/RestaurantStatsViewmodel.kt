package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class RestaurantStatsViewmodel:ReservantViewModel() {
    var isLoading: Boolean by mutableStateOf(false)
    var loadingError: Boolean by mutableStateOf(false)

}