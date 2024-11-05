package reservant_mobile.services

import androidx.paging.testing.asSnapshot
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.data.models.dtos.LocationDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import reservant_mobile.data.models.dtos.RestaurantGroupDTO
import reservant_mobile.data.models.dtos.ReviewDTO
import reservant_mobile.data.models.dtos.UnitOfMeasurement
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.RestaurantService
import java.time.LocalDateTime

class RestaurantServiceUnitTest: ServiceTest() {
    private val ser: IRestaurantService = RestaurantService()
    private val restaurant = RestaurantDTO(
        name = "Test restaurant",
        nip = "1060000062",
        restaurantType = "Restaurant",
        address = "Test address",
        city = "Test city",
        postalIndex = "01-001",
        provideDelivery = false,
        description = "Test desc",
        businessPermission = "test-jd.pdf",
        idCard = "test-jd.pdf",
        logo = "test-jd.png",
        location = LocationDTO(
            latitude = 51.0,
            longitude = 52.0
        ),
        maxReservationDurationMinutes = 30,
        openingHours = listOf(
            RestaurantDTO.AvailableHours(
            from = "10:00",
            until = "20:00"
        ))
    )
    private val restaurantGroup = RestaurantGroupDTO(
        name = "Test group",
        restaurantIds = listOf(1)
    )
    private val restaurantEmployee = RestaurantEmployeeDTO(
        login = "JohnTest",
        email = "test@email.com",
        firstName = "Johny",
        lastName = "Test",
        phoneNumber = "+48123456789",
        birthDate = "2001-01-01",
        password = "P@ssw0rd",
    )
    private val review = ReviewDTO(
        contents = "Test review",
        stars = 5
    )
    private val ingredient = IngredientDTO(
        publicName = "Test ing",
        unitOfMeasurement = UnitOfMeasurement.Gram,
        minimalAmount = 10.0,
        amountToOrder = 10.0,
        amount = 10.0,
        menuItem =  IngredientDTO.IngredientMenuItemDTO(
            menuItemId = 1,
            amountUsed = 10.0
        )
    )
    private val restaurantId = 1

    @Before
    fun setupData() = runBlocking {
        loginUser()
    }

    @Test
    fun get_restaurants_return_pagination()= runTest{
        val items = ser.getRestaurants().value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun get_restaurant_return_not_null()= runTest{
        assertThat(ser.getRestaurant(restaurantId).value).isNotNull()
    }

    @Test
    fun get_user_restaurant_return_not_null()= runTest{
        assertThat(ser.getUserRestaurant(restaurantId).value).isNotNull()
    }

    @Test
    fun register_and_delete_restaurant()= runTest{
        val res = ser.registerRestaurant(restaurant).value
        assertThat(res).isNotNull()
        assertThat(ser.deleteRestaurant(res!!.restaurantId).value).isTrue()
    }

    @Test
    fun validate_first_step_return_true()= runTest{
        assertThat(ser.validateFirstStep(restaurant).value).isTrue()
    }

    @Test
    fun edit_restaurant_return_not_null()= runTest{
        assertThat(ser.editRestaurant(restaurantId, restaurant).value).isNotNull()
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
//        TODO: uncomment when fixed server internal error
//        assertThat(id?.let { ser.deleteGroup(it).value }).isTrue()
    }

    @Test
    fun move_restaurant_to_group_return_not_null()= runTest{
        val groupId = ser.getGroups().value!!.last().restaurantGroupId

        assertThat(groupId?.let { ser.moveToGroup(restaurantId, it).value }).isNotNull()
    }

    @Test
    fun get_employees_return_not_null()= runTest{
        assertThat(ser.getUserEmployees().value).isNotNull()
    }

    @Test
    fun get_employee_return_not_null()= runTest{
        val id = ser.getUserEmployees().value!!.first().userId!!
        assertThat(ser.getEmployee(id).value).isNotNull()
    }

    @Test
    fun get_restaurant_employees_return_not_null()= runTest{
        assertThat(ser.getMyEmployees(restaurantId).value).isNotNull()
    }

    @Test
    fun create_add_and_remove_employee_from_restaurant()= runTest{
        val emp = ser.createEmployee(restaurantEmployee).value
        val empCopy = listOf(emp!!.copy(isHallEmployee = true))
        assertThat(empCopy).isNotNull()
        assertThat(ser.addEmployeeToRestaurant(restaurantId, empCopy).value).isTrue()
        val empId = ser.getMyEmployees(restaurantId).value!!.last().employmentId
        assertThat(ser.deleteEmployment(empId!!).value).isTrue()
    }

    @Test
    fun edit_employee_return_not_null()= runTest{
        val id = ser.getUserEmployees().value!!.last().userId!!
        assertThat(ser.editEmployee(id, restaurantEmployee).value).isNotNull()
    }

    @Test
    fun get_restaurant_tags_return_not_null() = runTest {
        assertThat(ser.getRestaurantTags().value).isNotNull()
    }

    @Test
    fun get_restaurant_orders_return_pagination()= runTest{
        val items = ser.getRestaurantOrders(restaurantId, returnFinished = true).value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun get_restaurant_events_return_pagination()= runTest{
        val items = ser.getRestaurantEvents(restaurantId).value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun get_restaurant_reviews_return_pagination()= runTest{
        val items = ser.getRestaurantReviews(restaurantId).value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun get_restaurant_review_return_not_null()= runTest{
        assertThat(ser.getRestaurantReview(1).value).isNotNull()
    }

    @Test
    fun add_edit_delete_review_return_not_null()= runTest{
        val rev = ser.addRestaurantReview(restaurantId, review).value
        assertThat(rev).isNotNull()
        assertThat(ser.editRestaurantReview(rev!!.reviewId!!, rev)).isNotNull()
        assertThat(ser.deleteRestaurantReview(rev.reviewId!!)).isNotNull()
    }

    @Test
    fun add_delete_review_restaurant_response_return_not_null()= runTest{
        val rev = ser.addRestaurantResponse(restaurantId, "Test restaurant response").value
        assertThat(rev).isNotNull()
        assertThat(ser.deleteRestaurantResponse(rev?.reviewId!!)).isNotNull()
    }

    @Test
    fun get_restaurant_visits_return_pagination()= runTest{
        val items = ser.getVisits(restaurantId).value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun get_restaurant_ingredients_return_pagination()= runTest{
        val items = ser.getIngredients(restaurantId).value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun get_restaurant_deliveries_return_pagination()= runTest{
        val items = ser.getDeliveries(restaurantId).value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

    @Test
    fun add_ingredient_return_not_null()= runTest{
        assertThat(ser.addIngredient(ingredient).value).isNotNull()
    }

    @Test
    fun edit_ingredient_return_not_null()= runTest{
        assertThat(ser.editIngredient(1,ingredient).value).isNotNull()
    }

    @Test
    fun correct_ingredient_return_not_null()= runTest{
        assertThat(ser.correctIngredient(
            ingredientId = 1,
            newAmount = 10.0,
            comment = "Test"
        ).value).isNotNull()
    }

    @Test
    fun get_available_hours_return_not_null()= runTest{
        val date = LocalDateTime.now()

        assertThat(ser.getAvailableHours(
            restaurantId = 1,
            date = date
        ).value).isNotNull()
    }

    @Test
    fun get_ingredient_history_return_pagination()= runTest{
        val items = ser.getIngredientHistory(1).value
        val itemsSnapshot = items?.asSnapshot {
            scrollTo(index = 10)
        }
        assertThat(itemsSnapshot).isNotEmpty()
    }

}