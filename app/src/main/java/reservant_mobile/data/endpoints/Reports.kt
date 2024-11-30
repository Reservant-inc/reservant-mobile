package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/reports")
class Reports(
    val dateFrom: String? = null,
    val dateUntil: String? = null,
    val category: String? = null,
    val reportedUserId: String? = null,
    val restaurantId: Int? = null,
) {

    @Resource("report-customer")
    class ReportCustomer(val parent: Reports = Reports())

    @Resource("report-employee")
    class ReportEmployee(val parent: Reports = Reports())

    @Resource("report-bug")
    class ReportBug(val parent: Reports = Reports())

    @Resource("report-lost-item")
    class ReportLostItem(val parent: Reports = Reports())

    @Resource("{reportId}")
    class ReportId(val parent: Reports = Reports(), val reportId: String) {
        @Resource("resolution")
        class Resolution(val parent: ReportId)
    }
}