package com.example.reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/restaurants")
class Restaurants {
    @Resource("in-area")
    class InArea(val parent: Restaurants = Restaurants(), val lat1: Double, val lon1: Double, val lat2: Double, val lon2: Double)
    @Resource("{restaurantId}")
    class Id(val parent: Restaurants = Restaurants(), val restaurantId: String){
        @Resource("orders")
        class Orders(val parent: Id)
        @Resource("events")
        class Events(val parent: Id)
        @Resource("reviews")
        class Reviews(val parent: Id)
    }
}