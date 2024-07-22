package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/restaurants")
class Restaurants(
    val origLat: Double? = null,
    val origLon: Double? = null,
    val name: String? = null,
    val tags: List<String>? = null,
    val lat1: Double? = null,
    val lon1: Double? = null,
    val lat2: Double? = null,
    val lon2: Double? = null,
    val page: Int? = null,
    val perPage: Int? = null
) {
    @Resource("{restaurantId}")
    class Id(val parent: Restaurants = Restaurants(), val restaurantId: String){
        @Resource("orders")
        class Orders(val parent: Id, val returnFinished:Boolean? = null, val page: Int? = null, val perPage: Int? = null, val orderBy: String? = null)
        @Resource("events")
        class Events(val parent: Id, val page: Int? = null, val perPage: Int? = null)
        @Resource("reviews")
        class Reviews(val parent: Id, val orderBy: String? = null, val page: Int? = null, val perPage: Int? = null)
    }
}