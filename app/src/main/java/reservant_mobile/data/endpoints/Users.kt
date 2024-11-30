package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/users")
class Users(val name: String? = null, val filter: String? = null, val page: Int? = null, val perPage: Int? = null) {

    @Resource("{employeeId}")
    class Id(val parent: Users = Users(), val employeeId: String)

    @Resource("{userId}")
    class UserId(val parent: Users = Users(), val userId: String){
        @Resource("ban")
        class Ban(val parent: UserId)

        @Resource("unban")
        class Unban(val parent: UserId)
    }
}