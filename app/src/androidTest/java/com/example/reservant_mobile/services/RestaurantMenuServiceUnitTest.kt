package com.example.reservant_mobile.services

import com.example.reservant_mobile.data.models.dtos.RestaurantMenuDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import com.example.reservant_mobile.data.services.IRestaurantMenuService
import com.example.reservant_mobile.data.services.RestaurantMenuService
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RestaurantMenuServiceUnitTest: ServiceTest() {
    private val ser: IRestaurantMenuService = RestaurantMenuService()

    private lateinit var menu: RestaurantMenuDTO
    private lateinit var menuItem: RestaurantMenuItemDTO

    @Before
    fun setupData() = runBlocking {
        loginUser()

        menu = RestaurantMenuDTO(
            restaurantId = 1,
            name = "Test Menu",
            alternateName = "Testovací menu",
            menuType = "Food",
            dateFrom = "2020-02-20"
        )

        menuItem = RestaurantMenuItemDTO(
            restaurantId = 1,
            price = 100.0,
            name = "Burger",
            alcoholPercentage = 99.9
        )
    }

    @Test
    fun add_and_delete_restaurant_menu()= runTest{
        val m = ser.addMenu(menu).value
        assertThat(m).isNotNull()
        assertThat(ser.deleteMenu(m!!.id!!).value).isTrue()
    }

    @Test
    fun get_menus_return_not_null()= runTest{
        assertThat(ser.getMenus(1).value).isNotNull()
    }

    @Test
    fun get_menu_return_not_null()= runTest{
        val id = ser.getMenus(1).value!!.first().id!!
        assertThat(ser.getMenu(id).value).isNotNull()
    }

    @Test
    fun edit_menu_return_not_null()= runTest{
        val id = ser.getMenus(1).value!!.last().id!!
        assertThat(ser.editMenu(id, menu).value).isNotNull()
    }

    @Test
    fun get_menu_items_return_not_null()= runTest{
        assertThat(ser.getMenuItems(1).value).isNotNull()
    }

    @Test
    fun get_menu_item_return_not_null()= runTest{
        val id = ser.getMenuItems(1).value!!.first().id!!
        assertThat(ser.getMenuItem(id).value).isNotNull()
    }

    @Test
    fun create_and_delete_menu_item()= runTest{
        val i = ser.createMenuItems(listOf(menuItem)).value
        assertThat(i).isNotNull()
        assertThat(ser.deleteMenuItem(i!!.first().id!!).value).isTrue()
    }

    @Test
    fun edit_menu_item_return_not_null()= runTest{
        val id = ser.getMenuItems(1).value!!.last().id!!
        assertThat(ser.editMenuItem(id, menuItem).value).isNotNull()
    }

    @Test
    fun add_items_to_menu_return_not_null()= runTest{
        val menuId = ser.getMenus(1).value!!.last().id!!
        val itemId = ser.getMenuItems(1).value!!.last().id!!
        assertThat(ser.addItemsToMenu(menuId, listOf(itemId)).value).isNotNull()
    }

}