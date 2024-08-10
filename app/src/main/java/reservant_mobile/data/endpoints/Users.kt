package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/users")
class Users(val name: String? = null, val page: Int? = null, val perPage: Int? = null) {

    @Resource("{employeeId}")
    class Id(val parent: Users = Users(), val employeeId: String)
}