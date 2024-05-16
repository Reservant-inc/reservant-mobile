package com.example.reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/my-restaurants")
class MyRestaurants {
    @Resource("{id}")
    class Id(val parent: MyRestaurants = MyRestaurants(), val id: String){
        @Resource("move-to-group")
        class MoveToGroup(val parent: Id)

        @Resource("employees")
        class Employees(val parent: Id)
    }

    @Resource("validate-first-step")
    class ValidateFirstStep(val parent: MyRestaurants = MyRestaurants())


}