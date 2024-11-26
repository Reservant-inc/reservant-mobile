package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/reports")
class Reports {

    @Resource("report-customer")
    class ReportCustomer(val parent: Reports = Reports())

    @Resource("report-employee")
    class ReportEmployee(val parent: Reports = Reports())

    @Resource("report-bug")
    class ReportBug(val parent: Reports = Reports())

    @Resource("report-lost-item")
    class ReportLostItem(val parent: Reports = Reports())
}