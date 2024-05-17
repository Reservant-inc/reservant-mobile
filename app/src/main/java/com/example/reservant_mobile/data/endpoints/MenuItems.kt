package com.example.reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/menu-items")
class MenuItems {
    @Resource("{id}")
    class Id(val parent: MenuItems = MenuItems(), val id: String)
}