package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/events")
class Events {
    @Resource("{eventId}")
    class Id(val parent: Events = Events(), val eventId: String){
        @Resource("interested")
        class Interested(val parent: Id, val page: Int? = null, val perPage: Int? = null)

        @Resource("accept-user/{userId}")
        class AcceptUser(val parent: Id, val userId: String)

        @Resource("reject-user/{userId}")
        class RejectUser(val parent: Id, val userId: String)
    }
}