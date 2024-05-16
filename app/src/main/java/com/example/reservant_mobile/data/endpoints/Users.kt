package com.example.reservant_mobile.data.endpoints

import com.example.reservant_mobile.data.services.UserService
import io.ktor.resources.Resource

@Resource("/users")
class Users {

    @Resource("{id}")
    class Id(val parent: Users = Users(), val id: String)
}