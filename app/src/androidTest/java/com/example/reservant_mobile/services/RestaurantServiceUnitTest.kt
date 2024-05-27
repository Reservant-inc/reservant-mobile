package com.example.reservant_mobile.services

import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantGroupDTO
import com.example.reservant_mobile.data.services.IRestaurantService
import com.example.reservant_mobile.data.services.RestaurantService
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RestaurantServiceUnitTest: ServiceTest() {
    private val ser: IRestaurantService = RestaurantService()

    private lateinit var restaurant: RestaurantDTO
    private lateinit var restaurantGroup: RestaurantGroupDTO
    private lateinit var restaurantEmployee: RestaurantEmployeeDTO

    @Before
    fun setupData() = runBlocking {
        loginUser()

        restaurant = RestaurantDTO(
            name = "Test restaurant",
            nip = "0224111111",
            restaurantType = "Restaurant",
            address = "Test address",
            city = "Test city",
            postalIndex = "01-001",
            provideDelivery = false,
            description = "Test desc",
            businessPermission = "306f9fa1-fda5-48c4-aa5f-7c7c375e065f.pdf",
            idCard = "306f9fa1-fda5-48c4-aa5f-7c7c375e065f.pdf",
            logo = "306f9fa1-fda5-48c4-aa5f-7c7c375e065f.png    "
        )

        restaurantGroup = RestaurantGroupDTO(
            name = "Test group",
            restaurantIds = listOf(1)
        )

        restaurantEmployee = RestaurantEmployeeDTO(
            login = "JohnTest",
            email = "test@email.com",
            firstName = "Johny",
            lastName = "Test",
            phoneNumber = "+48123456789",
            password = "P@ssw0rd",
        )
    }


    @Test
    fun get_restaurant_return_not_null()= runTest{
        assertThat(ser.getRestaurant(1).value).isNotNull()
    }

    @Test
    fun register_and_delete_restaurant()= runTest{
        assertThat(ser.registerRestaurant(restaurant).value).isTrue()
        val id = ser.getRestaurants().value!!.last().restaurantId
        assertThat(ser.deleteRestaurant(id).value).isTrue()
    }

    @Test
    fun validate_first_step_return_true()= runTest{
        assertThat(ser.validateFirstStep(restaurant).value).isTrue()
    }

    @Test
    fun edit_restaurant_return_not_null()= runTest{
        val id = ser.getRestaurants().value!!.last().restaurantId
        assertThat(ser.editRestaurant(id, restaurant).value).isNotNull()
    }

    @Test
    fun get_groups_return_not_null()= runTest{
        assertThat(ser.getGroups().value).isNotNull()
    }

    @Test
    fun get_group_return_not_null()= runTest{
        assertThat(ser.getGroup(1).value).isNotNull()
    }

    @Test
    fun add_and_delete_group_return_not_null()= runTest{
        assertThat(ser.addGroup(restaurantGroup).value).isTrue()
        val id = ser.getGroups().value!!.last().restaurantGroupId
        assertThat(id?.let { ser.deleteGroup(it).value }).isTrue()

    }

    @Test
    fun move_restaurant_to_group_return_not_null()= runTest{
        val restaurantId = ser.getRestaurants().value!!.last().restaurantId
        val groupId = ser.getGroups().value!!.last().restaurantGroupId

        assertThat(groupId?.let { ser.moveToGroup(restaurantId, it).value }).isNotNull()
    }

    @Test
    fun get_employees_return_not_null()= runTest{
        assertThat(ser.getEmployees().value).isNotNull()
    }

    @Test
    fun get_employee_return_not_null()= runTest{
        val id = ser.getEmployees().value!!.last().userId
        assertThat(ser.getEmployee(id).value).isNotNull()
    }

    @Test
    fun get_restaurant_employees_return_not_null()= runTest{
        val id = ser.getRestaurants().value!!.last().restaurantId
        assertThat(ser.getEmployees(id).value).isNotNull()
    }

//    @Test
//    fun create_employee_return_not_null()= runTest{
//        val emp = ser.createEmployee(restaurantEmployee).value
//        assertThat(emp).isNotNull()
//    }

    @Test
    fun add_and_remove_employee_from_restaurant()= runTest{
        val restaurantId = ser.getRestaurants().value!!.first().restaurantId
        assertThat(ser.addEmployeeToRestaurant(restaurantId, restaurantEmployee).value).isTrue()
        val empId = ser.getEmployees(restaurantId).value!!.last().employmentId
        assertThat(ser.deleteEmployment(empId!!).value).isTrue()

    }

    @Test
    fun edit_employee_return_not_null()= runTest{
        val id = ser.getEmployees().value!!.last().userId
        assertThat(ser.editEmployee(id, restaurantEmployee).value).isNotNull()
    }

    @Test
    fun get_restaurant_tags_return_not_null() = runTest {
        assertThat(ser.getRestaurantTags().value).isNotNull()
    }

    @Test
    fun get_restaurants_by_tag_return_not_null() = runTest {
        val tag = ser.getRestaurantTags().value!!.first()
        assertThat(ser.getRestaurantsByTag(tag).value).isNotNull()
    }
}