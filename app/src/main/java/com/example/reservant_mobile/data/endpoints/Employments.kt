package com.example.reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/employments")
class Employments {

    @Resource("{id}")
    class Id(val parent: Employments = Employments(), val id: String)
}