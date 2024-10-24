package reservant_mobile.services

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.services.IOrdersService
import reservant_mobile.data.services.OrdersService

class OrdersServiceUnitTest: ServiceTest(){
    private val ser:IOrdersService = OrdersService()

    private lateinit var order:OrderDTO
    private var orderId:Int = 1


    @Before
    fun setupData() = runBlocking {
        loginUser()

        order = OrderDTO(
            visitId = 1,
            note = "Test note",
            items = listOf(
                OrderDTO.OrderItemDTO(
                menuItemId = 2,
                amount = 1),
                OrderDTO.OrderItemDTO(
                    menuItemId = 2,
                    amount = 2),
            )
        )
    }

    @Test
    fun get_order_return_not_null()= runTest{
        assertThat(ser.getOrder(orderId).value).isNotNull()
    }

    @Test
    fun cancel_order_return_true()= runTest{
        assertThat(ser.cancelOrder(orderId).value).isNotNull()
    }

    @Test
    fun create_order_return_not_null()= runTest{
        assertThat(ser.createOrder(order).value).isNotNull()
    }

//    todo: does not work as JD
    @Test
    fun set_order_status_return_not_null()= runTest{
        assertThat(ser.changeOrderStatus(orderId, order).value).isNotNull()
    }
}