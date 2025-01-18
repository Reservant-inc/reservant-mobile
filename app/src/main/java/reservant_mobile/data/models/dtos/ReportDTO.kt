package reservant_mobile.data.models.dtos

import com.example.reservant_mobile.R
import kotlinx.serialization.Serializable

@Serializable
data class ReportDTO(
    val reportId: Int? = null,
    val description: String? = null,
    val reportDate: String? = null,
    val category: ReportCategory? = null,
    val createdBy: ReportParticipant? = null,
    val reportedUser: ReportParticipant? = null,
    val escalatedBy: ReportParticipant? = null,
    val escalationComment: String? = null,
    val resolvedBy: ReportParticipant? = null,
    /***
     * Date in 'yyyy-MM-d'T'H:mm:ss.SSS'Z'' format
     */
    val resolutionDate: String? = null,
    val assignedAgents: List<AssignedAgents>? = null,
    val resolutionComment: String? = null,
    val reportedUserId: String? = reportedUser?.userId,
    val visit: VisitDTO? = null,
    val visitId: Int? = visit?.visitId,
    val threadId: Int? = null,
    val reportStatus: ReportStatus? = null
){
    @Serializable
    data class ReportParticipant(
        val userId: String,
        val firstName: String,
        val lastName: String,
        val photo: String? = null,
        val isArchived: Boolean = false
    )

    @Serializable
    data class AssignedAgents(
        val agent: ReportParticipant,
        val from: String? = null,
        val until: String? = null,
    )

    @Serializable
    enum class ReportCategory(val stringId: Int){
        Technical(R.string.label_report_category_technical),
        LostItem(R.string.label_report_category_lost_item),
        RestaurantEmployeeReport(R.string.label_report_category_restaurant_employee),
        CustomerReport(R.string.label_report_category_customer)
    }

    @Serializable
    enum class ReportStatus(val stringId: Int){
        All(R.string.label_all),
        NotResolved(R.string.label_unresolved),
        ResolvedPositively(R.string.label_report_resolved_positively),
        ResolvedNegatively(R.string.label_report_resolved_negatively)
    }

}