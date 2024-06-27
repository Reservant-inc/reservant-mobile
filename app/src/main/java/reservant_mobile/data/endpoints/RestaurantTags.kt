package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/restaurant-tags")
class RestaurantTags {

    @Resource("{tag}")
    class Tag(
        val parent: RestaurantTags = RestaurantTags(),
        val tag: String
    ){
        @Resource("restaurants")
        class Restaurants(val parent: Tag)
    }
}