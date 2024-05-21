package com.example.reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/my-restaurant-groups")
class MyRestaurantGroups {

    @Resource("{id}")
    class Id(val parent: MyRestaurantGroups = MyRestaurantGroups(), val id: String)

}