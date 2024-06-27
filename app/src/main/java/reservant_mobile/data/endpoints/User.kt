package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/user")
class User {

    @Resource("employees")
    class Employees(val parent: User = User())
}