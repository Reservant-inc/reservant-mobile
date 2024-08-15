package reservant_mobile.data.services

import androidx.paging.PagingData
import com.example.reservant_mobile.R
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import reservant_mobile.data.endpoints.Auth
import reservant_mobile.data.endpoints.Employments
import reservant_mobile.data.endpoints.MyRestaurantGroups
import reservant_mobile.data.endpoints.MyRestaurants
import reservant_mobile.data.endpoints.RestaurantTags
import reservant_mobile.data.endpoints.Restaurants
import reservant_mobile.data.endpoints.User
import reservant_mobile.data.endpoints.Users
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.models.dtos.PageDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import reservant_mobile.data.models.dtos.RestaurantGroupDTO
import reservant_mobile.data.models.dtos.ReviewDTO
import reservant_mobile.data.models.dtos.fields.Result

interface IRestaurantService{
    suspend fun registerRestaurant(restaurant: RestaurantDTO): Result<RestaurantDTO?>
    suspend fun validateFirstStep(restaurant: RestaurantDTO): Result<Boolean>
    suspend fun getRestaurants(): Result<List<RestaurantDTO>?>
    suspend fun getRestaurant(id:Any): Result<RestaurantDTO?>
    suspend fun getUserRestaurant(id:Any): Result<RestaurantDTO?>
    suspend fun editRestaurant(id: Any, restaurant: RestaurantDTO): Result<RestaurantDTO?>
    suspend fun deleteRestaurant(id: Any): Result<Boolean>
    suspend fun getGroups(): Result<List<RestaurantGroupDTO>?>
    suspend fun getGroup(id:Any): Result<RestaurantGroupDTO?>
    suspend fun addGroup(group: RestaurantGroupDTO): Result<Boolean>
    suspend fun editGroup(id: Any, newName: String): Result<RestaurantGroupDTO?>
    suspend fun deleteGroup(id: Any): Result<Boolean>
    suspend fun moveToGroup(restaurantId: Any, groupId: Any): Result<RestaurantDTO?>
    suspend fun createEmployee(emp: RestaurantEmployeeDTO): Result<RestaurantEmployeeDTO?>
    suspend fun addEmployeeToRestaurant(id: Any, emp: RestaurantEmployeeDTO): Result<Boolean>
    suspend fun getEmployees(restaurantId: Any): Result<List<RestaurantEmployeeDTO>?>
    suspend fun getEmployees(): Result<List<RestaurantEmployeeDTO>?>
    suspend fun getEmployee(id: Any): Result<RestaurantEmployeeDTO?>
    suspend fun editEmployee(id: Any, emp: RestaurantEmployeeDTO): Result<RestaurantEmployeeDTO?>
    suspend fun deleteEmployment(employmentId: Int): Result<Boolean>
    suspend fun getRestaurantTags(): Result<List<String>?>
    suspend fun getRestaurantsByTag(tag:String): Result<List<RestaurantDTO>?>
    suspend fun getRestaurantsInArea(lat1:Double, lon1:Double, lat2:Double, lon2:Double): Result<Flow<PagingData<RestaurantDTO>>?>
    suspend fun getRestaurantOrders(restaurantId: Any,  returnFinished:Boolean? = null, orderBy: String? = null): Result<Flow<PagingData<OrderDTO>>?>
    suspend fun getRestaurantEvents(restaurantId: Any): Result<Flow<PagingData<EventDTO>>?>
    suspend fun addRestaurantReview(restaurantId: Any, review: ReviewDTO): Result<ReviewDTO?>
    suspend fun getRestaurantReviews(restaurantId: Any, orderBy: String? = null): Result<Flow<PagingData<ReviewDTO>>?>
    }

@OptIn(InternalSerializationApi::class)
class RestaurantService(): ServiceUtil(), IRestaurantService {

    override suspend fun registerRestaurant(restaurant: RestaurantDTO): Result<RestaurantDTO?> {
        val res = api.post(MyRestaurants(), restaurant)
        return complexResultWrapper(res)
    }

    override suspend fun validateFirstStep(restaurant: RestaurantDTO): Result<Boolean> {
        val res = api.post(MyRestaurants.ValidateFirstStep(), restaurant)
        return booleanResultWrapper(res)
    }

    override suspend fun getRestaurants(): Result<List<RestaurantDTO>?> {
        val res = api.get(MyRestaurants())
        return complexResultWrapper(res)
    }

    override suspend fun getRestaurant(id: Any): Result<RestaurantDTO?> {
        val res = api.get(Restaurants.Id(restaurantId = id.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun getUserRestaurant(id: Any): Result<RestaurantDTO?> {
        val res = api.get(MyRestaurants.Id(id = id.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun editRestaurant(id: Any, restaurant: RestaurantDTO): Result<RestaurantDTO?> {
        val res = api.put(MyRestaurants.Id(id = id.toString()), restaurant)
        return complexResultWrapper(res)
    }

    override suspend fun deleteRestaurant(id: Any): Result<Boolean> {
        val res = api.delete(MyRestaurants.Id(id = id.toString()))
        return booleanResultWrapper(res)
    }

    override suspend fun getGroups(): Result<List<RestaurantGroupDTO>?> {
        val res = api.get(MyRestaurantGroups())
        return complexResultWrapper(res)
    }

    override suspend fun getGroup(id:Any): Result<RestaurantGroupDTO?> {
        val res = api.get(MyRestaurantGroups.Id(id = id.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun addGroup(group: RestaurantGroupDTO): Result<Boolean> {
        val res = api.post(MyRestaurantGroups(), group)
        return booleanResultWrapper(res, HttpStatusCode.Created)
    }

    override suspend fun editGroup(id: Any, newName: String): Result<RestaurantGroupDTO?> {
        val newGroup: HashMap<String, String> = hashMapOf("name" to newName)
        val res = api.put(MyRestaurantGroups.Id(id = id.toString()), newGroup)
        return complexResultWrapper(res)
    }

    override suspend fun deleteGroup(id: Any): Result<Boolean> {
        val res = api.delete(MyRestaurantGroups.Id(id = id.toString()))
        return booleanResultWrapper(res)
    }

    override suspend fun moveToGroup(restaurantId: Any, groupId: Any): Result<RestaurantDTO?> {
        val newGroup: HashMap<String, String> = hashMapOf("groupId" to groupId.toString())
        val res = api.post(
            MyRestaurants.Id.MoveToGroup(
            parent  = MyRestaurants.Id(id = restaurantId.toString())
        ), newGroup)
        return complexResultWrapper(res)
    }

    override suspend fun createEmployee(emp: RestaurantEmployeeDTO): Result<RestaurantEmployeeDTO?> {
        val res = api.post(Auth.RegisterRestaurantEmployee(), emp)
        return complexResultWrapper(res)
    }

    override suspend fun addEmployeeToRestaurant(id: Any, emp: RestaurantEmployeeDTO): Result<Boolean> {
        val res = api.post(
            MyRestaurants.Id.Employees(
            parent = MyRestaurants.Id(id = id.toString())
        ), listOf(emp))
        return booleanResultWrapper(res, HttpStatusCode.NoContent)
    }

    override suspend fun getEmployees(restaurantId: Any): Result<List<RestaurantEmployeeDTO>?> {
        val res = api.get(
            MyRestaurants.Id.Employees(
            parent = MyRestaurants.Id(id = restaurantId.toString())
        ))
        return complexResultWrapper(res)
    }

    override suspend fun getEmployees(): Result<List<RestaurantEmployeeDTO>?> {
        val res = api.get(User.Employees())
        return complexResultWrapper(res)
    }

    override suspend fun getEmployee(id: Any): Result<RestaurantEmployeeDTO?> {
        val res = api.get(Users.Id(employeeId = id.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun editEmployee(id: Any, emp: RestaurantEmployeeDTO): Result<RestaurantEmployeeDTO?> {
        val res = api.put(Users.Id(employeeId = id.toString()), emp)
        return complexResultWrapper(res)
    }

    override suspend fun deleteEmployment(employmentId: Int): Result<Boolean> {
        val res = api.delete(Employments.Id(id = employmentId.toString()))
        return booleanResultWrapper(res, HttpStatusCode.NoContent)
    }

    override suspend fun getRestaurantTags(): Result<List<String>?> {
        val res = api.get(RestaurantTags())
        return complexResultWrapper(res)
    }

    override suspend fun getRestaurantsByTag(tag: String): Result<List<RestaurantDTO>?> {
        val res = api.get(RestaurantTags.Tag.Restaurants(parent = RestaurantTags.Tag(tag = tag)))
        return complexResultWrapper(res)
    }

    override suspend fun getRestaurantsInArea(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Result<Flow<PagingData<RestaurantDTO>>?> {

        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Restaurants(
//                lat1 = lat1,
//                lon1 = lon1,
//                lat2 = lat2,
//                lon2 = lon2,
                page = page,
                perPage = perPage
            ))}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(RestaurantDTO::class.serializer()))
        val flow = sps.getFlow()

        return if(flow != null)
            Result(isError = false, value = sps.getFlow())
        else
            Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
    }

    override suspend fun getRestaurantOrders(
        restaurantId: Any,
        returnFinished: Boolean?,
        orderBy: String?
    ): Result<Flow<PagingData<OrderDTO>>?> {


        val call: suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage ->
            api.get(
                Restaurants.Id.Orders(
                    parent = Restaurants.Id(restaurantId = restaurantId.toString()),
                    returnFinished = returnFinished,
                    page = page,
                    perPage = perPage,
                    orderBy = orderBy
                )
            )
        }

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(OrderDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun getRestaurantEvents(restaurantId: Any): Result<Flow<PagingData<EventDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Restaurants.Id.Events(
                parent = Restaurants.Id(restaurantId = restaurantId.toString()),
                page = page,
                perPage = perPage
            ))}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(EventDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun addRestaurantReview(restaurantId: Any, review: ReviewDTO): Result<ReviewDTO?> {
        val res = api.post(Restaurants.Id.Reviews(
            parent = Restaurants.Id(restaurantId = restaurantId.toString())),
            review
        )
        return complexResultWrapper(res)
    }

    override suspend fun getRestaurantReviews(restaurantId: Any, orderBy: String?): Result<Flow<PagingData<ReviewDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Restaurants.Id.Reviews(
                parent = Restaurants.Id(restaurantId = restaurantId.toString()),
                orderBy = orderBy,
                page = page,
                perPage = perPage
            ))}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(ReviewDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }
}
