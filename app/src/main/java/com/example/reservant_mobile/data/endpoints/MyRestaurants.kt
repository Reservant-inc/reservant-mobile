package com.example.reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/my-restaurants")
class MyRestaurants {
    @Resource("{id}")
    class Id(val parent: MyRestaurants = MyRestaurants(), val id: Long)
}