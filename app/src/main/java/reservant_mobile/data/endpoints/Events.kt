package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/events")
class Events {
    @Resource("{eventId}")
    class Id(val parent: Events = Events(), val eventId: String){
        @Resource("interested")
        class Interested(val parent: Id)
    }
}