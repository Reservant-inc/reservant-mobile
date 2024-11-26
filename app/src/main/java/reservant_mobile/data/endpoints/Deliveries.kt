package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/deliveries")
class Deliveries {

    @Resource("{deliveryId}")
    class DeliveryId(val parent: Deliveries = Deliveries(), val deliveryId: String){

        @Resource("confirm-delivered")
        class ConfirmDelivered(val parent: DeliveryId)

        @Resource("mark-canceled")
        class MarkCanceled(val parent: DeliveryId)
    }
}