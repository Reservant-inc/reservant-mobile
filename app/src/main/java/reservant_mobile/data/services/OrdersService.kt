package reservant_mobile.data.services

import com.example.reservant_mobile.R
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import reservant_mobile.data.endpoints.Orders
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.models.dtos.fields.Result

interface IOrdersService{
    suspend fun getOrder(orderId: Any): Result<OrderDTO?>
    suspend fun cancelOrder(orderId: Any): Result<Boolean>
    suspend fun createOrder(orderId: Any): Result<OrderDTO?>
    suspend fun changeOrderStatus(orderId: Any, order: OrderDTO): Result<OrderDTO?>
}

class OrdersService(private var api: APIService = APIService()): IOrdersService {
    override suspend fun getOrder(orderId: Any): Result<OrderDTO?> {
        val res = api.get(Orders.OrderId(orderId=orderId.toString()))

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

    override suspend fun cancelOrder(orderId: Any): Result<Boolean> {
        val res = api.post(Orders.OrderId.Cancel(parent = Orders.OrderId(orderId=orderId.toString())), "")
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        if (res.value!!.status == HttpStatusCode.OK)
            return Result(isError = false, value = true)

        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), false)
    }

    override suspend fun createOrder(orderId: Any): Result<OrderDTO?> {
        val res = api.post(Orders.OrderId(orderId=orderId.toString()), "")

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

    override suspend fun changeOrderStatus(orderId: Any, order: OrderDTO): Result<OrderDTO?> {
        val res = api.put(Orders.OrderId(orderId=orderId.toString()), order)

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