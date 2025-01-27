package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/visits")
class Visits {
    @Resource("{visitId}")
    class VisitID(val parent: Visits = Visits(), val visitId: String){
        @Resource("approve")
        class Approve(val parent: VisitID)

        @Resource("decline")
        class Decline(val parent: VisitID)

        @Resource("confirm-start")
        class ConfirmStart(val parent: VisitID)

        @Resource("confirm-end")
        class ConfirmEnd(val parent: VisitID)

        @Resource("table")
        class Table(val parent: VisitID)
    }
    @Resource("guests")
    class Guests(val parent: Visits = Visits())
}