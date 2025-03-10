package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/my-restaurants")
class MyRestaurants {
    @Resource("{restaurantId}")
    class Id(val parent: MyRestaurants = MyRestaurants(), val restaurantId: String){
        @Resource("move-to-group")
        class MoveToGroup(val parent: Id)

        @Resource("employees")
        class Employees(val parent: Id)

        @Resource("menus")
        class Menus(val parent: Id)

        @Resource("menu-items")
        class MenuItems(val parent: Id)

        @Resource("reports")
        class Reports(val parent: Id,
                      val dateFrom: String? = null,
                      val dateUntil: String? = null,
                      val category: String? = null,
                      val reportedUserId: String? = null,
                      val createdById: String? = null,
                      val assignedToId: String? = null,
                      val page: Int? = null,
                      val perPage: Int? = null
            )

        @Resource("statistics")
        class Statistics(val parent: Id,
                         val dateFrom: String? = null,
                         val dateUntil: String? = null,
                         val popularItemMaxCount: Int? = null)

        @Resource("tables")
        class Tables(val parent: Id)
    }

    @Resource("validate-first-step")
    class ValidateFirstStep(val parent: MyRestaurants = MyRestaurants())

    @Resource("statistics")
    class Statistics(val parent: MyRestaurants = MyRestaurants(),
                     val dateFrom: String? = null,
                     val dateUntil: String? = null,
                     val popularItemMaxCount: Int? = null)

}