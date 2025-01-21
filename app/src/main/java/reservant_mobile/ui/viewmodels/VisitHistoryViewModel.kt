import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.UserService
import reservant_mobile.ui.viewmodels.ReservantViewModel

class VisitHistoryViewModel(
    private val userService: IUserService = UserService()
) : ReservantViewModel() {

    // Backing field for the paging flow
    private val _visitHistoryFlow = MutableStateFlow<Flow<PagingData<VisitDTO>>?>(null)
    val visitHistoryFlow: Flow<PagingData<VisitDTO>>?
        get() = _visitHistoryFlow.value

    /**
     * Call this to start loading the user's visit history
     */
    fun loadVisitHistory() {
        viewModelScope.launch {
            val result = userService.getUserVisitHistory()
            if (!result.isError && result.value != null) {
                // Cache in viewModelScope for better paging performance
                _visitHistoryFlow.value = result.value!!.cachedIn(viewModelScope)
            } else {
                _visitHistoryFlow.value = null
            }
        }
    }
}
