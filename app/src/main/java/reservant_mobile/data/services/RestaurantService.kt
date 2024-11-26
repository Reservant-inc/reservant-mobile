package reservant_mobile.data.services

import androidx.paging.PagingData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import reservant_mobile.data.endpoints.Auth
import reservant_mobile.data.endpoints.Employments
import reservant_mobile.data.endpoints.Ingredients
import reservant_mobile.data.endpoints.MyRestaurantGroups
import reservant_mobile.data.endpoints.MyRestaurants
import reservant_mobile.data.endpoints.Reports
import reservant_mobile.data.endpoints.RestaurantTags
import reservant_mobile.data.endpoints.Restaurants
import reservant_mobile.data.endpoints.Reviews
import reservant_mobile.data.endpoints.User
import reservant_mobile.data.endpoints.Users
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.models.dtos.PageDTO
import reservant_mobile.data.models.dtos.ReportDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import reservant_mobile.data.models.dtos.RestaurantGroupDTO
import reservant_mobile.data.models.dtos.ReviewDTO
import reservant_mobile.data.models.dtos.TableDTO
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.utils.GetDeliveriesSort
import reservant_mobile.data.utils.GetIngredientsSort
import reservant_mobile.data.utils.GetReservationStatus
import reservant_mobile.data.utils.GetRestaurantOrdersSort
import reservant_mobile.data.utils.GetRestaurantReviewsSort
import reservant_mobile.data.utils.GetVisitsSort
import java.time.LocalDateTime

interface IRestaurantService{
    suspend fun registerRestaurant(restaurant: RestaurantDTO): Result<RestaurantDTO?>
    suspend fun validateFirstStep(restaurant: RestaurantDTO): Result<Boolean>
    suspend fun getRestaurants(origLat: Double? = null,
                               origLon: Double? = null,
                               name: String? = null,
                               tags: List<String>? = null,
                               minRating: Int?= null,
                               lat1: Double? = null,
                               lon1: Double? = null,
                               lat2: Double? = null,
                               lon2: Double? = null,):Result<Flow<PagingData<RestaurantDTO>>?>
    suspend fun getRestaurant(id:Any):  Result<RestaurantDTO?>
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
    suspend fun addEmployeeToRestaurant(id: Any, emp: List<RestaurantEmployeeDTO>): Result<Boolean>
    suspend fun getMyEmployees(restaurantId: Any): Result<List<RestaurantEmployeeDTO>?>
    suspend fun getUserEmployees(): Result<List<RestaurantEmployeeDTO>?>
    suspend fun getEmployees(
        restaurantId: Any,
        hallOnly: Boolean? = null,
        backdoorOnly: Boolean? = null): Result<List<RestaurantEmployeeDTO>?>
    suspend fun getEmployee(id: Any): Result<RestaurantEmployeeDTO?>
    suspend fun editEmployee(id: Any, emp: RestaurantEmployeeDTO): Result<RestaurantEmployeeDTO?>
    suspend fun deleteEmployment(employmentId: Int): Result<Boolean>
    suspend fun editEmployment(empList: List<RestaurantEmployeeDTO>): Result<Boolean>
    suspend fun getRestaurantTags(): Result<List<String>?>
    /***
     * Available order values : see GetRestaurantOrders class
     */
    suspend fun getRestaurantOrders(restaurantId: Any,  returnFinished:Boolean? = null, orderBy: GetRestaurantOrdersSort? = null): Result<Flow<PagingData<OrderDTO>>?>
    suspend fun getRestaurantEvents(restaurantId: Any): Result<Flow<PagingData<EventDTO>>?>
    suspend fun addRestaurantReview(restaurantId: Any, review: ReviewDTO): Result<ReviewDTO?>
    suspend fun getRestaurantReview(reviewId: Any): Result<ReviewDTO?>

    /***
     * Available order values : see GetRestaurantReviewsSort class
     */
    suspend fun getRestaurantReviews(restaurantId: Any, orderBy: GetRestaurantReviewsSort? = null): Result<Flow<PagingData<ReviewDTO>>?>
    suspend fun editRestaurantReview(reviewId: Any, review: ReviewDTO): Result<ReviewDTO?>
    suspend fun deleteRestaurantReview(reviewId: Any): Result<Boolean>
    suspend fun addRestaurantResponse(reviewId: Any, response: String): Result<ReviewDTO?>
    suspend fun deleteRestaurantResponse(reviewId: Any): Result<Boolean>

    /***
     * Available visitSorting values : see GetVisitsSort class
     */
    suspend fun getVisits(restaurantId: Any,
                          dateStart: LocalDateTime? = null,
                          dateEnd: LocalDateTime? = null,
                          tableId: Int? = null,
                          hasOrders: Boolean? = null,
                          isTakeaway: Boolean? = null,
                          reservationStatus: GetReservationStatus? = null,
                          orderBy: GetVisitsSort? = null): Result<Flow<PagingData<VisitDTO>>?>

    /***
     * Available order values : see GetIngredientsSort class
     */
    suspend fun getIngredients(restaurantId: Any, orderBy: GetIngredientsSort? = null): Result<List<IngredientDTO>?>

    /***
     * Available order values : see GetDeliveriesSort class
     */
    suspend fun getDeliveries(restaurantId: Any,
                              returnDelivered: Boolean? = null,
                              userId: String? = null,
                              userName: String? = null,
                              orderBy: GetDeliveriesSort? = null): Result<Flow<PagingData<DeliveryDTO>>?>

    suspend fun addIngredient(ingredient: IngredientDTO): Result<IngredientDTO?>
    suspend fun editIngredient(ingredientId: Any, ingredient: IngredientDTO): Result<IngredientDTO?>
    suspend fun correctIngredient(ingredientId: Any, newAmount: Double, comment: String): Result<IngredientDTO.CorrectionDTO?>
    suspend fun getAvailableHours(restaurantId: Any, date: LocalDateTime? = null, numberOfGuests: Int? = null): Result<List<RestaurantDTO.AvailableHours>?>
    suspend fun getIngredientHistory(ingredientId: Any,
                                     dateFrom: LocalDateTime? = null,
                                     dateUntil: LocalDateTime? = null,
                                     userId: String? = null,
                                     comment: String? = null): Result<Flow<PagingData<IngredientDTO.CorrectionDTO>>?>
    suspend fun getCurrentTables(restaurantId: Any): Result<List<TableDTO>?>

}

@OptIn(InternalSerializationApi::class)
class RestaurantService(): ServiceUtil(), IRestaurantService {

    override suspend fun registerRestaurant(restaurant: RestaurantDTO): Result<RestaurantDTO?> {
        val res = api.post(MyRestaurants(), restaurant)
        return complexResultWrapper(res)
    }

    override suspend fun validateFirstStep(restaurant: RestaurantDTO): Result<Boolean> {
        val res = api.post(MyRestaurants.ValidateFirstStep(), restaurant)
        return booleanResultWrapper(res, HttpStatusCode.NoContent)
    }

    override suspend fun getRestaurants(
        origLat: Double?,
        origLon: Double?,
        name: String?,
        tags: List<String>?,
        minRating: Int?,
        lat1: Double?,
        lon1: Double?,
        lat2: Double?,
        lon2: Double?
    ): Result<Flow<PagingData<RestaurantDTO>>?> {
        val call: suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage ->
            api.get(
                Restaurants(
                    origLat = origLat,
                    origLon = origLon,
                    name = name,
                    tags = tags,
                    minRating = minRating,
                    lat1 = lat1,
                    lon1 = lon1,
                    lat2 = lat2,
                    lon2 = lon2,
                    page = page,
                    perPage = perPage,
                )
            )
        }

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(RestaurantDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun getRestaurant(id: Any): Result<RestaurantDTO?> {
        val res = api.get(Restaurants.Id(restaurantId = id.toString()))
        return complexResultWrapper(res)
    }


    override suspend fun getUserRestaurant(id: Any): Result<RestaurantDTO?> {
        val res = api.get(MyRestaurants.Id(restaurantId = id.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun editRestaurant(id: Any, restaurant: RestaurantDTO): Result<RestaurantDTO?> {
        val res = api.put(MyRestaurants.Id(restaurantId = id.toString()), restaurant)
        return complexResultWrapper(res)
    }

    override suspend fun deleteRestaurant(id: Any): Result<Boolean> {
        val res = api.delete(MyRestaurants.Id(restaurantId = id.toString()))
        return booleanResultWrapper(res, HttpStatusCode.NoContent)
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
        return booleanResultWrapper(res)
    }

    override suspend fun editGroup(id: Any, newName: String): Result<RestaurantGroupDTO?> {
        val newGroup: HashMap<String, String> = hashMapOf("name" to newName)
        val res = api.put(MyRestaurantGroups.Id(id = id.toString()), newGroup)
        return complexResultWrapper(res)
    }

    override suspend fun deleteGroup(id: Any): Result<Boolean> {
        val res = api.delete(MyRestaurantGroups.Id(id = id.toString()))
        return booleanResultWrapper(res, HttpStatusCode.NoContent)
    }

    override suspend fun moveToGroup(restaurantId: Any, groupId: Any): Result<RestaurantDTO?> {
        val newGroup: HashMap<String, String> = hashMapOf("groupId" to groupId.toString())
        val res = api.post(
            MyRestaurants.Id.MoveToGroup(
            parent  = MyRestaurants.Id(restaurantId = restaurantId.toString())
        ), newGroup)
        return complexResultWrapper(res)
    }

    override suspend fun createEmployee(emp: RestaurantEmployeeDTO): Result<RestaurantEmployeeDTO?> {
        val res = api.post(Auth.RegisterRestaurantEmployee(), emp)
        return complexResultWrapper(res)
    }

    override suspend fun addEmployeeToRestaurant(id: Any, emp: List<RestaurantEmployeeDTO>): Result<Boolean> {
        val res = api.post(
            MyRestaurants.Id.Employees(
            parent = MyRestaurants.Id(restaurantId = id.toString())
        ), listOf(emp))
        return booleanResultWrapper(res, HttpStatusCode.NoContent)
    }

    override suspend fun getMyEmployees(restaurantId: Any): Result<List<RestaurantEmployeeDTO>?> {
        val res = api.get(
            MyRestaurants.Id.Employees(
            parent = MyRestaurants.Id(restaurantId = restaurantId.toString())
        ))
        return complexResultWrapper(res)
    }

    override suspend fun getUserEmployees(): Result<List<RestaurantEmployeeDTO>?> {
        val res = api.get(User.Employees())
        return complexResultWrapper(res)
    }

    override suspend fun getEmployees(
        restaurantId: Any,
        hallOnly: Boolean?,
        backdoorOnly: Boolean?
    ): Result<List<RestaurantEmployeeDTO>?> {
        val res = api.get(Restaurants.Id.Employees(
                parent = Restaurants.Id(restaurantId = restaurantId.toString()),
                hallOnly = hallOnly,
                backdoorOnly = backdoorOnly
            ))
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

    override suspend fun editEmployment(empList: List<RestaurantEmployeeDTO>): Result<Boolean> {
        val res = api.put(Employments(), empList)
        return booleanResultWrapper(res, HttpStatusCode.NoContent)
    }

    override suspend fun getRestaurantTags(): Result<List<String>?> {
        val res = api.get(RestaurantTags())
        return complexResultWrapper(res)
    }

    override suspend fun getRestaurantOrders(
        restaurantId: Any,
        returnFinished: Boolean?,
        orderBy: GetRestaurantOrdersSort?
    ): Result<Flow<PagingData<OrderDTO>>?> {
        val call: suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage ->
            api.get(
                Restaurants.Id.Orders(
                    parent = Restaurants.Id(restaurantId = restaurantId.toString()),
                    returnFinished = returnFinished,
                    page = page,
                    perPage = perPage,
                    orderBy = orderBy?.toString()
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

    override suspend fun getRestaurantReview(reviewId: Any): Result<ReviewDTO?> {
        val res = api.get(Reviews.ReviewId(reviewId = reviewId.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun getRestaurantReviews(restaurantId: Any, orderBy: GetRestaurantReviewsSort?): Result<Flow<PagingData<ReviewDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Restaurants.Id.Reviews(
                parent = Restaurants.Id(restaurantId = restaurantId.toString()),
                orderBy = orderBy?.toString(),
                page = page,
                perPage = perPage
            ))}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(ReviewDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun editRestaurantReview(
        reviewId: Any,
        review: ReviewDTO
    ): Result<ReviewDTO?> {
        val res = api.put(Reviews.ReviewId(reviewId = reviewId.toString()), review)
        return complexResultWrapper(res)
    }

    override suspend fun deleteRestaurantReview(reviewId: Any): Result<Boolean> {
        val res = api.delete(Reviews.ReviewId(reviewId = reviewId.toString()))
        return booleanResultWrapper(res)
    }

    override suspend fun addRestaurantResponse(reviewId: Any, response: String): Result<ReviewDTO?> {
        val body = mapOf(
            "restaurantResponseText" to response
        )
        val res = api.put(Reviews.ReviewId.RestaurantResponse(
            parent = Reviews.ReviewId(reviewId = reviewId.toString())
        ), body)
        return complexResultWrapper(res)
    }

    override suspend fun deleteRestaurantResponse(reviewId: Any): Result<Boolean> {
        val res = api.delete(Reviews.ReviewId.RestaurantResponse(
            parent = Reviews.ReviewId(reviewId = reviewId.toString())
        ))
        return booleanResultWrapper(res, expectedCode = HttpStatusCode.NoContent)
    }

    override suspend fun getVisits(
        restaurantId: Any,
        dateStart: LocalDateTime?,
        dateEnd: LocalDateTime?,
        tableId: Int?,
        hasOrders: Boolean?,
        isTakeaway: Boolean?,
        reservationStatus: GetReservationStatus?,
        orderBy: GetVisitsSort?
    ): Result<Flow<PagingData<VisitDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Restaurants.Id.Visits(
                parent = Restaurants.Id(restaurantId = restaurantId.toString()),
                dateStart = dateStart?.toString(),
                dateEnd = dateEnd?.toString(),
                tableId = tableId,
                hasOrders = hasOrders,
                isTakeaway = isTakeaway,
                reservationStatus = reservationStatus.toString(),
                visitSorting = orderBy?.toString(),
                page = page,
                perPage = perPage
            ))}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(VisitDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun getIngredients(
        restaurantId: Any,
        orderBy: GetIngredientsSort?
    ): Result<List<IngredientDTO>?> {
        val res = api.get(
            Restaurants.Id.Ingredients(
                parent = Restaurants.Id(restaurantId = restaurantId.toString()),
                orderBy = orderBy?.toString()
            )
        )
        return complexResultWrapper(res)
    }

    override suspend fun getDeliveries(
        restaurantId: Any,
        returnDelivered: Boolean?,
        userId: String?,
        userName: String?,
        orderBy: GetDeliveriesSort?
    ): Result<Flow<PagingData<DeliveryDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Restaurants.Id.Deliveries(
                parent = Restaurants.Id(restaurantId = restaurantId.toString()),
                returnDelivered = returnDelivered,
                userId = userId,
                userName = userName,
                orderBy = orderBy?.toString(),
                page = page,
                perPage = perPage
            ))}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(DeliveryDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun addIngredient(ingredient: IngredientDTO): Result<IngredientDTO?> {
        val res = api.post(Ingredients(), ingredient)
        return complexResultWrapper(res)
    }

    override suspend fun editIngredient(
        ingredientId: Any,
        ingredient: IngredientDTO
    ): Result<IngredientDTO?> {
        val res = api.put(Ingredients.IngredientId(ingredientId = ingredientId.toString()), ingredient)
        return complexResultWrapper(res)
    }

    override suspend fun correctIngredient(
        ingredientId: Any,
        newAmount: Double,
        comment: String
    ): Result<IngredientDTO.CorrectionDTO?> {
        val correction = IngredientDTO.CorrectionDTO(
            newAmount = newAmount,
            comment = comment
        )
        val res = api.post(Ingredients.IngredientId.CorrectAmount(parent = Ingredients.IngredientId(ingredientId = ingredientId.toString())), correction)
        return complexResultWrapper(res)
    }

    override suspend fun getAvailableHours(
        restaurantId: Any,
        date: LocalDateTime?,
        numberOfGuests: Int?
    ): Result<List<RestaurantDTO.AvailableHours>?> {
        val res  = api.get(Restaurants.Id.AvailableHours(
            parent = Restaurants.Id(restaurantId = restaurantId.toString()),
            date = date.toString(),
            numberOfGuests = numberOfGuests
        ))
        return complexResultWrapper(res)
    }

    override suspend fun getIngredientHistory(
        ingredientId: Any,
        dateFrom: LocalDateTime?,
        dateUntil: LocalDateTime?,
        userId: String?,
        comment: String?
    ): Result<Flow<PagingData<IngredientDTO.CorrectionDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Ingredients.IngredientId.History(
                parent = Ingredients.IngredientId(ingredientId = ingredientId.toString()),
                dateFrom = dateFrom?.toString(),
                dateUntil = dateUntil?.toString(),
                userId = userId,
                comment = comment,
                page = page,
                perPage = perPage
            ))}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(IngredientDTO.CorrectionDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun getCurrentTables(restaurantId: Any): Result<List<TableDTO>?> {
        val res  = api.get(Restaurants.Id.Tables(
            parent = Restaurants.Id(restaurantId = restaurantId.toString()),
        ))
        return complexResultWrapper(res)
    }
}
