package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/events")
class Events(
    val origLat: Double? = null,
    val origLon: Double? = null,
    val restaurantId: Int? = null,
    val restaurantName: String? = null,
    val name: String? = null,
    val dateFrom: String? = null,
    val dateUntil: String? = null,
    val eventStatus: String? = null,
    val page: Int? = null,
    val perPage: Int? = null
) {
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