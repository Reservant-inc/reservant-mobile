import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.models.dtos.ReportDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.IReportsService
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.ReportsService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.services.UserService
import reservant_mobile.ui.viewmodels.ReservantViewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import reservant_mobile.data.models.dtos.UserSummaryDTO

class TicketViewModel(
    private val reportsService: IReportsService = ReportsService(),
    private val userService: IUserService = UserService(),
    private val restaurantService: IRestaurantService = RestaurantService()
) : ReservantViewModel() {

    // Common text: "description"
    var description by mutableStateOf("")

    // For picking a visit
    var isPickVisitDialogOpen by mutableStateOf(false)
    var userVisits = mutableStateListOf<VisitDTO>()
    var selectedVisit: VisitDTO? by mutableStateOf(null)

    // For picking a participant inside the selected visit
    var isPickParticipantDialogOpen by mutableStateOf(false)
    var participantList = mutableStateListOf<UserDTO>() // or simpler
    var selectedParticipant: UserDTO? by mutableStateOf(null)
    var selectedEmployee: UserSummaryDTO? by mutableStateOf(null)

    // Possibly store success/failure
    var showSuccessDialog by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Paged flow for reports
    private val _reportsFlow = MutableStateFlow<Flow<PagingData<ReportDTO>>?>(null)
    val reportsFlow: StateFlow<Flow<PagingData<ReportDTO>>?> = _reportsFlow.asStateFlow()

    private val _visitPagingFlow = MutableStateFlow<Flow<PagingData<VisitDTO>>?>(null)
    val visitPagingFlow: StateFlow<Flow<PagingData<VisitDTO>>?> = _visitPagingFlow.asStateFlow()


    suspend fun getPhoto(url: String): Bitmap?{
        val result = fileService.getImage(url)
        if (!result.isError){
            return result.value!!
        }
        return null
    }
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


    // For employees, fetch visits from a restaurant. For customers, fetch personal visits.
    // This is a simplified example.
    fun loadVisitsForUserOrRestaurant() {
        viewModelScope.launch {
            val isEmployee = Roles.RESTAURANT_EMPLOYEE in UserService.UserObject.roles
            val visitsResult = if (isEmployee) {
                // Example: load visits for a specific restaurantId = 7
                restaurantService.getVisits(restaurantId = 7)
            } else {
                // For a normal user, get their visit history
                userService.getUserVisitHistory()
            }

            if (!visitsResult.isError && visitsResult.value != null) {
                // We have a Flow<PagingData<VisitDTO>>
                // Cache it in the ViewModel scope:
                _visitPagingFlow.value = visitsResult.value.cachedIn(viewModelScope)
            } else {
                // If error, set to null or handle
                _visitPagingFlow.value = null
            }
        }
    }

    fun loadParticipantsFromVisit(visit: VisitDTO) {
        participantList.clear()
        // "participants" -> map them to a list of userDTO
        visit.participants?.forEach { part ->
            participantList.add(
                UserDTO(
                    userId = part.userId,
                    firstName = part.firstName ?: "" ,
                    lastName = part.lastName ?: "",
                    // etc.
                )
            )
        }
    }

    // 1) "reportCustomer"
    fun sendReportCustomer() {
        val vId = selectedVisit?.visitId ?: return
        val userId = selectedParticipant?.userId ?: return
        val desc = description
        viewModelScope.launch {
            val result = reportsService.reportCustomer(
                description = desc,
                reportedUserId = userId,
                visitId = vId
            )
            handleReportResult(result)
        }
    }

    // 2) "reportEmployee"
    fun sendReportEmployee() {
        val vId = selectedVisit?.visitId ?: return
        val employeeId = selectedEmployee?.userId ?: return
        val desc = description
        viewModelScope.launch {
            val result = reportsService.reportEmployee(
                description = desc,
                reportedUserId = employeeId,
                visitId = vId
            )
            handleReportResult(result)
        }
    }

    // 3) "reportLostItem"
    fun sendReportLostItem() {
        val vId = selectedVisit?.visitId ?: return
        val desc = description
        viewModelScope.launch {
            val result = reportsService.reportLostItem(
                description = desc,
                visitId = vId
            )
            handleReportResult(result)
        }
    }

    // 4) "reportBug"
    fun sendReportBug() {
        val desc = description
        viewModelScope.launch {
            val result = reportsService.reportBug(desc)
            handleReportResult(result)
        }
    }

    private fun handleReportResult(res: Result<ReportDTO?>) {
        if (!res.isError && res.value != null) {
            showSuccessDialog = true
        } else {
            errorMessage = "Failed to send report"
        }
    }
}
