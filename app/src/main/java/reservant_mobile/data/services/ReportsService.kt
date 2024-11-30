package reservant_mobile.data.services

import reservant_mobile.data.endpoints.Reports
import reservant_mobile.data.models.dtos.ReportDTO
import reservant_mobile.data.models.dtos.fields.Result
import java.time.LocalDateTime

interface IReportsService{
    suspend fun reportCustomer(description: String, reportedUserId: String, visitId: Int ): Result<ReportDTO?>
    suspend fun reportEmployee(description: String, reportedUserId: String, visitId: Int ): Result<ReportDTO?>
    suspend fun reportLostItem(description: String, visitId: Int ): Result<ReportDTO?>
    suspend fun reportBug(description: String): Result<ReportDTO?>
    suspend fun getReports(dateFrom: LocalDateTime? = null,
                           dateUntil: LocalDateTime? = null,
                           category: ReportDTO.ReportCategory? = null,
                           reportedUserId: String? = null,
                           restaurantId: Int? = null): Result<List<ReportDTO>?>
}

class ReportsService(): ServiceUtil(), IReportsService {
    override suspend fun reportCustomer(
        description: String,
        reportedUserId: String,
        visitId: Int
    ): Result<ReportDTO?> {
        val dto = ReportDTO(
            description = description,
            reportedUserId = reportedUserId,
            visitId = visitId
        )
        val res  = api.post(Reports.ReportCustomer(), dto)
        return complexResultWrapper(res)
    }

    override suspend fun reportEmployee(
        description: String,
        reportedUserId: String,
        visitId: Int
    ): Result<ReportDTO?> {
        val dto = ReportDTO(
            description = description,
            reportedUserId = reportedUserId,
            visitId = visitId
        )
        val res  = api.post(Reports.ReportEmployee(), dto)
        return complexResultWrapper(res)
    }

    override suspend fun reportLostItem(description: String, visitId: Int): Result<ReportDTO?> {
        val dto = ReportDTO(
            description = description,
            visitId = visitId
        )
        val res  = api.post(Reports.ReportLostItem(), dto)
        return complexResultWrapper(res)
    }

    override suspend fun reportBug(description: String): Result<ReportDTO?> {
        val dto = ReportDTO(
            description = description,
        )
        val res  = api.post(Reports.ReportBug(), dto)
        return complexResultWrapper(res)
    }

    override suspend fun getReports(
        dateFrom: LocalDateTime?,
        dateUntil: LocalDateTime?,
        category: ReportDTO.ReportCategory?,
        reportedUserId: String?,
        restaurantId: Int?
    ): Result<List<ReportDTO>?> {
        val res  = api.get(Reports(
            dateFrom = dateFrom?.toString(),
            dateUntil = dateUntil?.toString(),
            category = category?.name,
            reportedUserId = reportedUserId,
            restaurantId = restaurantId
        ))
        return complexResultWrapper(res)
    }
}