package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.endpoints.Auth
import com.example.reservant_mobile.data.endpoints.Employments
import com.example.reservant_mobile.data.endpoints.MyRestaurantGroups
import com.example.reservant_mobile.data.endpoints.MyRestaurants
import com.example.reservant_mobile.data.endpoints.RestaurantTags
import com.example.reservant_mobile.data.endpoints.User
import com.example.reservant_mobile.data.endpoints.Users
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantGroupDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

interface IRestaurantService{
    suspend fun registerRestaurant(restaurant: RestaurantDTO): Result<Boolean>
    suspend fun validateFirstStep(restaurant: RestaurantDTO): Result<Boolean>
    suspend fun getRestaurants():Result<List<RestaurantDTO>?>
    suspend fun getRestaurant(id:Any): Result<RestaurantDTO?>
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
    suspend fun getEmployees():  Result<List<RestaurantEmployeeDTO>?>
    suspend fun getEmployee(id: Any):  Result<RestaurantEmployeeDTO?>
    suspend fun editEmployee(id: Any, emp: RestaurantEmployeeDTO):  Result<RestaurantEmployeeDTO?>
    suspend fun deleteEmployment(id: Any): Result<Boolean>
    suspend fun getRestaurantTags(): Result<List<String>?>
    suspend fun getRestaurantsByTag(tag:String): Result<List<RestaurantDTO>?>
    }

class RestaurantService(private var api: APIService = APIService()): IRestaurantService {

    override suspend fun registerRestaurant(restaurant: RestaurantDTO): Result<Boolean> {
        val res = api.post(MyRestaurants(), restaurant)
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        if (res.value!!.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)

    }

    override suspend fun validateFirstStep(restaurant: RestaurantDTO): Result<Boolean> {
        val res = api.post(MyRestaurants.ValidateFirstStep(), restaurant)
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        if (res.value!!.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun getRestaurants(): Result<List<RestaurantDTO>?>  {
        val res = api.get(MyRestaurants())

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }
        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }
    override suspend fun getRestaurant(id: Any): Result<RestaurantDTO?>  {
        val res = api.get(MyRestaurants.Id(id = id.toString()))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun editRestaurant(id: Any, restaurant: RestaurantDTO): Result<RestaurantDTO?> {
        val res = api.put(MyRestaurants.Id(id = id.toString()), restaurant)

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK)
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun deleteRestaurant(id: Any): Result<Boolean>  {
        val res = api.delete(MyRestaurants.Id(id = id.toString()))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        if (res.value!!.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun getGroups(): Result<List<RestaurantGroupDTO>?>{
        val res = api.get(MyRestaurantGroups())

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }
    override suspend fun getGroup(id:Any): Result<RestaurantGroupDTO?>{
        val res = api.get(MyRestaurantGroups.Id(id = id.toString()))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun addGroup(group: RestaurantGroupDTO): Result<Boolean> {
        val res = api.post(MyRestaurantGroups(), group)

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        if (res.value!!.status == HttpStatusCode.Created)
            return Result(isError = false, value = true)

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun editGroup(id: Any, newName: String): Result<RestaurantGroupDTO?>{
        val newGroup: HashMap<String, String> = hashMapOf("name" to newName)
        val res = api.put(MyRestaurantGroups.Id(id = id.toString()), newGroup)

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK)
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun deleteGroup(id: Any): Result<Boolean> {
        val res = api.delete(MyRestaurantGroups.Id(id = id.toString()))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        if (res.value!!.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)    }

    override suspend fun moveToGroup(restaurantId: Any, groupId: Any): Result<RestaurantDTO?> {

    val newGroup: HashMap<String, String> = hashMapOf("groupId" to groupId.toString())
        val res = api.post(MyRestaurants.Id.MoveToGroup(
            parent  = MyRestaurants.Id(id = restaurantId.toString())
        ), newGroup)

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK)
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun createEmployee(emp: RestaurantEmployeeDTO): Result<RestaurantEmployeeDTO?>{
        val res = api.post(Auth.RegisterRestaurantEmployee(), emp)

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }
    override suspend fun addEmployeeToRestaurant(id: Any, emp: RestaurantEmployeeDTO): Result<Boolean>{
        val res = api.post(MyRestaurants.Id.Employees(
            parent = MyRestaurants.Id(id = id.toString())
        ), emp)

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        if (res.value!!.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }
    override suspend fun getEmployees(restaurantId: Any): Result<List<RestaurantEmployeeDTO>?>{
        val res = api.get(MyRestaurants.Id.Employees(
            parent = MyRestaurants.Id(id = restaurantId.toString())
        ))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun getEmployees(): Result<List<RestaurantEmployeeDTO>?> {
        val res = api.get(User.Employees())

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun getEmployee(id: Any): Result<RestaurantEmployeeDTO?> {
        val res = api.get(Users.Id(id=id.toString()))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun editEmployee(id: Any, emp: RestaurantEmployeeDTO): Result<RestaurantEmployeeDTO?> {
        val res = api.put(Users.Id(id = id.toString()), emp)

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun deleteEmployment(id: Any): Result<Boolean> {
        val res = api.delete(Employments.Id(id = id.toString()))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        if (res.value!!.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun getRestaurantTags(): Result<List<String>?> {
        val res = api.get(RestaurantTags())

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun getRestaurantsByTag(tag: String): Result<List<RestaurantDTO>?> {
        val res = api.get(RestaurantTags.Tag.Restaurants(parent = RestaurantTags.Tag(tag = tag)))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

}
