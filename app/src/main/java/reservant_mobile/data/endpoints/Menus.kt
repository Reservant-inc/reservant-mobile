package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/menus")
class Menus {
    @Resource("{id}")
    class Id(val parent: Menus = Menus(), val id: String){
        @Resource("items")
        class Items(val parent: Id)
    }
    @Resource("menu-types")
    class MenuTypes(val parent: Menus = Menus())
}