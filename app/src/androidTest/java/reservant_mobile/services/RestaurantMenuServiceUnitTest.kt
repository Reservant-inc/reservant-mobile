package reservant_mobile.services

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.data.models.dtos.RestaurantMenuDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.services.IRestaurantMenuService
import reservant_mobile.data.services.RestaurantMenuService

class RestaurantMenuServiceUnitTest: ServiceTest() {
    private val ser: IRestaurantMenuService = RestaurantMenuService()
    private val restaurantId = 4

    private lateinit var menu: RestaurantMenuDTO
    private lateinit var menuItem: RestaurantMenuItemDTO

    @Before
    fun setupData() = runBlocking {
        loginUser()

        menu = RestaurantMenuDTO(
            restaurantId = 4,
            name = "Test Menu",
            alternateName = "Testovací menu",
            menuType = "Food",
            dateFrom = "2020-02-20",
            menuItemsIds = emptyList()
        )

        menuItem = RestaurantMenuItemDTO(
            restaurantId = 4,
            price = 100.0,
            name = "Burger",
            alcoholPercentage = 99.9,
            photo = "test-jd.png",
            ingredients = listOf(IngredientDTO(
                ingredientId = 1,
                amountUsed = 10.0
            ))
        )
    }

    @Test
    fun add_and_delete_restaurant_menu()= runTest{
        val m = ser.addMenu(menu).value
        assertThat(m).isNotNull()
        assertThat(ser.deleteMenu(m!!.menuId!!).value).isTrue()
    }

    @Test
    fun get_owner_menus_return_not_null()= runTest{
        assertThat(ser.getOwnerMenus(restaurantId).value).isNotNull()
    }

    @Test
    fun get_menus_return_not_null()= runTest{
        assertThat(ser.getMenus(1).value).isNotNull()
    }

    @Test
    fun get_menu_return_not_null()= runTest{
        val id = ser.getMenus(1).value!!.first().menuId!!
        assertThat(ser.getMenu(id).value).isNotNull()
    }

    @Test
    fun edit_menu_return_not_null()= runTest{
        val id = ser.getMenus(restaurantId).value!!.last().menuId!!
        assertThat(ser.editMenu(id, menu).value).isNotNull()
    }

    @Test
    fun get_menu_items_return_not_null()= runTest{
        assertThat(ser.getMenuItems(restaurantId).value).isNotNull()
    }

    @Test
    fun get_owner_menu_items_return_not_null()= runTest{
        assertThat(ser.getOwnerMenuItems(1).value).isNotNull()
    }

    @Test
    fun get_menu_item_return_not_null()= runTest{
        val id = ser.getMenuItems(restaurantId).value!!.first().menuItemId!!
        assertThat(ser.getMenuItem(id).value).isNotNull()
    }

    @Test
    fun create_and_delete_menu_item()= runTest{
        val i = ser.createMenuItem(menuItem).value
        assertThat(i).isNotNull()
        assertThat(ser.deleteMenuItem(i!!.menuItemId!!).value).isTrue()
    }

    @Test
    fun edit_menu_item_return_not_null()= runTest{
        val id = ser.getMenuItems(restaurantId).value!!.last().menuItemId!!
        assertThat(ser.editMenuItem(id, menuItem).value).isNotNull()
    }

    @Test
    fun add_items_to_menu_return_not_null()= runTest{
        val menuId = ser.getMenus(restaurantId).value!!.last().menuId!!
        val itemId = ser.getMenuItems(restaurantId).value!!.last().menuItemId!!
        assertThat(ser.addItemsToMenu(menuId, listOf(itemId)).value).isNotNull()
    }

}