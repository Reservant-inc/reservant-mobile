package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/user")
class User {

    @Resource("employees")
    class Employees(val parent: User = User())

    @Resource("visits")
    class Visits(val parent: User = User())

    @Resource("visit-history")
    class VisitHistory(val parent: User = User())

    @Resource("events-created")
    class EventsCreated(val parent: User = User())

    @Resource("{employeeId}")
    class EmployeeId(val parent: User = User(), val employeeId: String)

    @Resource("events-interested-in")
    class EventsInterestedIn(val parent: User = User())

    @Resource("threads")
    class Threads(val parent: User = User())
}