package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/orders")
class Orders {

    @Resource("{orderId}")
    class OrderId(val parent: Orders = Orders(), val orderId: String){
        @Resource("cancel")
        class Cancel(val parent: OrderId)

        @Resource("status")
        class Status(val parent: OrderId)
    }
}