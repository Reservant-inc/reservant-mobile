package reservant_mobile.data.services

import reservant_mobile.data.endpoints.Orders
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.models.dtos.fields.Result

interface IOrdersService{
    suspend fun getOrder(orderId: Any): Result<OrderDTO?>
    suspend fun cancelOrder(orderId: Any): Result<Boolean>
    suspend fun createOrder(order: OrderDTO): Result<OrderDTO?>
    suspend fun changeOrderStatus(orderId: Any, order: OrderDTO): Result<OrderDTO?>
}

class OrdersService():ServiceUtil(), IOrdersService {
    override suspend fun getOrder(orderId: Any): Result<OrderDTO?> {
        val res = api.get(Orders.OrderId(orderId=orderId.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun cancelOrder(orderId: Any): Result<Boolean> {
        val res = api.post(Orders.OrderId.Cancel(parent = Orders.OrderId(orderId=orderId.toString())), "")
        return booleanResultWrapper(res)
    }

    override suspend fun createOrder(order: OrderDTO): Result<OrderDTO?> {
        val res = api.post(Orders(), order)
        return complexResultWrapper(res)
    }

    override suspend fun changeOrderStatus(orderId: Any, order: OrderDTO): Result<OrderDTO?> {
        val res = api.put(Orders.OrderId(orderId=orderId.toString()), order)
        return complexResultWrapper(res)
    }
}