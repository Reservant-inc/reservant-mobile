package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/my-restaurant-groups")
class MyRestaurantGroups {

    @Resource("{id}")
    class Id(val parent: MyRestaurantGroups = MyRestaurantGroups(), val id: String){
        @Resource("statistics ")
        class Statistics(val parent: Id,
                         val dateFrom: String? = null,
                         val dateUntil: String? = null,
                         val popularItemMaxCount: Int? = null)
    }

}