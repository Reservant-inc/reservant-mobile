package com.example.reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/auth")
class Auth {

    @Resource("register-restaurant-employee")
    class RegisterRestaurantEmployee(val parent: Auth = Auth())
}