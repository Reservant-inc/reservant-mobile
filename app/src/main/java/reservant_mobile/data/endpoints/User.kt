package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/user")
class User {

    @Resource("employees")
    class Employees(val parent: User = User())

    @Resource("visits")
    class Visits(val parent: User = User(), val page: Int? = null, val perPage: Int? = null)

    @Resource("visit-history")
    class VisitHistory(val parent: User = User(), val page: Int? = null, val perPage: Int? = null)

    @Resource("events-created")
    class EventsCreated(val parent: User = User())

    @Resource("{employeeId}")
    class EmployeeId(val parent: User = User(), val employeeId: String)

    @Resource("events-interested-in")
    class EventsInterestedIn(val parent: User = User(), val page: Int? = null, val perPage: Int? = null)

    @Resource("threads")
    class Threads(val parent: User = User(), val page: Int? = null, val perPage: Int? = null)
}