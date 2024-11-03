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

    @Resource("{employeeId}")
    class EmployeeId(val parent: User = User(), val employeeId: String)

    @Resource("events")
    class Events(
        val parent: User = User(),
        val page: Int? = null,
        val perPage: Int? = null,
        val dateFrom: String? = null,
        val dateUntil: String? = null,
        val category: String? = null,
        val order: String? = null
    )

    @Resource("threads")
    class Threads(val parent: User = User(), val page: Int? = null, val perPage: Int? = null)

    @Resource("employments")
    class Employments(val parent: User = User(), val returnTerminated: Boolean? = null)

    @Resource("settings")
    class Settings(val parent: User = User())
}