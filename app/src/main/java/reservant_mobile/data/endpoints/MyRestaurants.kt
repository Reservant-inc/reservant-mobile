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
    }

    @Resource("validate-first-step")
    class ValidateFirstStep(val parent: MyRestaurants = MyRestaurants())


}