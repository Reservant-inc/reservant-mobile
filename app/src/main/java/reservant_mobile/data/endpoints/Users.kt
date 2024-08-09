package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/users")
class Users {

    @Resource("{employeeId}")
    class Id(val parent: Users = Users(), val employeeId: String)
}