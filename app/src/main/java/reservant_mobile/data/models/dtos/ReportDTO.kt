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
    val reportedUserId: String? = reportedUser?.userId,
    val visitId: Int? = null
){
    @Serializable
    data class ReportParticipant(
        val userId: String,
        val firstName: String,
        val lastName: String,
        val photo: String? = null
    )

    @Serializable
    enum class ReportCategory(val stringId: Int){
        Technical(R.string.label_report_category_technical),
        LostItem(R.string.label_report_category_lost_item),
        RestaurantEmployeeReport(R.string.label_report_category_restaurant_employee),
        CustomerReport(R.string.label_report_category_customer)
    }
}