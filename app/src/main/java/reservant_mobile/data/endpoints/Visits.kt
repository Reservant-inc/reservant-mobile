package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/visits")
class Visits {
    @Resource("{visitId}")
    class VisitID(val parent: Visits = Visits(), val visitId: String)
}