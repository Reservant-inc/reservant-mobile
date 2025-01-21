import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.IVisitsService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.services.VisitsService
import reservant_mobile.ui.viewmodels.ReservantViewModel

class VisitHistoryViewModel(
    private val userService: IUserService = UserService(),
    private val visitsService: IVisitsService = VisitsService()
) : ReservantViewModel() {

    // For PAST visits
    private val _visitHistoryFlow = MutableStateFlow<Flow<PagingData<VisitDTO>>?>(null)
    val visitHistoryFlow: Flow<PagingData<VisitDTO>>?
        get() = _visitHistoryFlow.value

    // For UPCOMING visits
    private val _futureVisitsFlow = MutableStateFlow<Flow<PagingData<VisitDTO>>?>(null)
    val futureVisitsFlow: Flow<PagingData<VisitDTO>>?
        get() = _futureVisitsFlow.value

    // Single-visit detail
    private val _visit = MutableStateFlow<VisitDTO?>(null)
    val visit: StateFlow<VisitDTO?> = _visit.asStateFlow()

    fun loadVisitHistory() {
        viewModelScope.launch {
            val result = userService.getUserVisitHistory()
            if (!result.isError && result.value != null) {
                _visitHistoryFlow.value = result.value.cachedIn(viewModelScope)
            } else {
                _visitHistoryFlow.value = null
            }
        }
    }

    fun loadUpcomingVisits() {
        viewModelScope.launch {
            val result = userService.getUserVisits()
            if (!result.isError && result.value != null) {
                _futureVisitsFlow.value = result.value.cachedIn(viewModelScope)
            } else {
                _futureVisitsFlow.value = null
            }
        }
    }

    fun loadVisit(visitId: Int) {
        viewModelScope.launch {
            val result = visitsService.getVisit(visitId)
            if (!result.isError && result.value != null) {
                _visit.value = result.value
            } else {
                _visit.value = null
            }
        }
    }
}
