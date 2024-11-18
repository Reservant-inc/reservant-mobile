package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/reports")
class Reports {

    @Resource("report-customer")
    class ReportCustomer(val parent: Reports = Reports())
}