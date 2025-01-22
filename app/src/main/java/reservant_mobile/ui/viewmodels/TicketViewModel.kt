package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.ReportDTO
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.UserService

class TicketViewModel(
    private val userService: IUserService = UserService()
) : ReservantViewModel() {
    var topic by mutableStateOf("")
    var category by mutableStateOf("")
    var messageContent by mutableStateOf("")

    // Paged flow for reports
    private val _reportsFlow = MutableStateFlow<Flow<PagingData<ReportDTO>>?>(null)
    val reportsFlow: StateFlow<Flow<PagingData<ReportDTO>>?> = _reportsFlow.asStateFlow()

    fun loadReports(reportStatus: String) {
        val status = if (reportStatus == "All") null
        else ReportDTO.ReportStatus.valueOf(reportStatus)

        viewModelScope.launch {
            val result = userService.getReports(status = status)
            if (!result.isError && result.value != null) {
                _reportsFlow.value = result.value.cachedIn(viewModelScope)
            } else {
                _reportsFlow.value = null
            }
        }
    }

    fun isTopicInvalid(): Boolean {
        return topic.isBlank()
    }

    fun isCategoryInvalid(): Boolean {
        return category.isBlank()
    }

    fun isMessageContentInvalid(): Boolean {
        return messageContent.isBlank()
    }
}
