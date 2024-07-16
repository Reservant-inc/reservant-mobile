package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/restaurants")
class Restaurants {
    @Resource("in-area")
    class InArea(val parent: Restaurants = Restaurants(), val lat1: Double, val lon1: Double, val lat2: Double, val lon2: Double)
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