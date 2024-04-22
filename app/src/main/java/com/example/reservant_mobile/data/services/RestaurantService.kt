package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RegisterRestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantGroupDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.ui.constants.Endpoints
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

interface IRestaurantService{
    suspend fun registerRestaurant(restaurant: RegisterRestaurantDTO): Result<Boolean>
    suspend fun validateFirstStep(restaurant: RestaurantDTO): Result<Boolean>
    suspend fun getRestaurants():Result<List<RestaurantDTO>?>
    suspend fun getRestaurant(id:Any): Result<RestaurantDTO?>
    suspend fun editRestaurant(id: Any, restaurant: RestaurantDTO): Result<Boolean>
    suspend fun deleteRestaurant(id: Int): Result<Boolean>
    suspend fun getGroups(): Result<List<RestaurantGroupDTO>?>
    suspend fun getGroup(id:Any): Result<RestaurantGroupDTO?>
    suspend fun addGroup(group: RestaurantGroupDTO): Result<Boolean>
    suspend fun editGroup(id: Any, newName: String): Result<Boolean>
    suspend fun moveToGroup(restaurantId: Any, groupId: Any): Result<Boolean>
    suspend fun createEmployee(emp: RestaurantEmployeeDTO): Result<RestaurantEmployeeDTO?>
    suspend fun addEmployeeToRestaurant(id: Any, emp: RestaurantEmployeeDTO): Result<Boolean>
    suspend fun getEmployees(restaurantId: Any): Result<List<RestaurantEmployeeDTO>?>
    }

class RestaurantService(private var api: APIService = APIServiceImpl()): IRestaurantService {

    override suspend fun registerRestaurant(restaurant: RegisterRestaurantDTO): Result<Boolean> {
        val res = api.post(restaurant, Endpoints.MY_RESTAURANTS) ?:
            return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), false)

        if (res.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = false)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)

    }

    override suspend fun validateFirstStep(restaurant: RestaurantDTO): Result<Boolean> {
        val res = api.post(restaurant, Endpoints.RESTAURANT_VALIDATE_STEP) ?:
        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), false)

        if (res.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = false)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun getRestaurants():Result<List<RestaurantDTO>?>  {
        val res = api.get(Endpoints.MY_RESTAURANTS) ?:
            return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), null)


        if (res.status == HttpStatusCode.OK){
            return try {
                val j:List<RestaurantDTO> = res.body()
                Result(isError = false, value = j)
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }
    override suspend fun getRestaurant(id:Any): Result<RestaurantDTO?>  {
        val res = api.get(Endpoints.MY_RESTAURANT(id.toString())) ?:
            return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), null)


        if (res.status == HttpStatusCode.OK){
            return try {
                val j:RestaurantDTO = res.body()
                Result(isError = false, value = j)
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)

    }

    override suspend fun editRestaurant(id: Any, restaurant: RestaurantDTO): Result<Boolean>  {
        val res = api.put( restaurant ,Endpoints.MY_RESTAURANT(id.toString())) ?:
        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), false)


        if (res.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = false)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun deleteRestaurant(id: Int): Result<Boolean>  {
        val res = api.delete(Endpoints.MY_RESTAURANT(id.toString())) ?:
        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), false)


        if (res.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = false)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun getGroups(): Result<List<RestaurantGroupDTO>?>{
        val res = api.get(Endpoints.MY_RESTAURANT_GROUPS) ?:
        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), null)


        if (res.status == HttpStatusCode.OK){
            return try {
                val j:List<RestaurantGroupDTO> = res.body()
                Result(isError = false, value = j)
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }
    override suspend fun getGroup(id:Any): Result<RestaurantGroupDTO?>{
        val res = api.get(Endpoints.MY_RESTAURANT_GROUP(id.toString())) ?:
        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), null)


        if (res.status == HttpStatusCode.OK){
            return try {
                val j:RestaurantGroupDTO = res.body()
                Result(isError = false, value = j)
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun addGroup(group: RestaurantGroupDTO): Result<Boolean> {
        val res = api.post(group, Endpoints.MY_RESTAURANT_GROUPS) ?:
        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), false)

        if (res.status == HttpStatusCode.Created)
            return Result(isError = false, value = true)

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = false)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)    }

    override suspend fun editGroup(id: Any, newName: String): Result<Boolean>{
        val newGroup: HashMap<String, String> = hashMapOf("name" to newName)
        val res = api.put( newGroup ,Endpoints.MY_RESTAURANT_GROUP(id.toString())) ?:
        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), false)


        if (res.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = false)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun moveToGroup(restaurantId: Any, groupId: Any): Result<Boolean> {

    val newGroup: HashMap<String, String> = hashMapOf("groupId" to groupId.toString())
        val res = api.post( newGroup ,Endpoints.MOVE_RESTAURANT_TO_GROUP(restaurantId.toString())) ?:
        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), false)


        if (res.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = false)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun createEmployee(emp: RestaurantEmployeeDTO): Result<RestaurantEmployeeDTO?>{
        val res = api.post(emp, Endpoints.REGISTER_RESTAURANT_EMPLOYEE) ?:
        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), null)

        if (res.status == HttpStatusCode.OK){
            return try {
                val j:RestaurantEmployeeDTO = res.body()
                Result(isError = false, value = j)
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }
    override suspend fun addEmployeeToRestaurant(id: Any, emp: RestaurantEmployeeDTO): Result<Boolean>{
        val res = api.post(emp, Endpoints.MY_RESTAURANT_EMPLOYEES(id.toString())) ?:
        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), false)

        if (res.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = false)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }
    override suspend fun getEmployees(restaurantId: Any): Result<List<RestaurantEmployeeDTO>?>{
        val res = api.get(Endpoints.MY_RESTAURANT_EMPLOYEES(restaurantId.toString())) ?:
        return Result(true, mapOf(pair= Pair("TOAST", R.string.error_connection_server)), null)


        if (res.status == HttpStatusCode.OK){
            return try {
                val j:List<RestaurantEmployeeDTO> = res.body()
                Result(isError = false, value = j)
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)


        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

}