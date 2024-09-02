package reservant_mobile.data.services

import reservant_mobile.data.endpoints.Deliveries
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.data.models.dtos.fields.Result


interface IDeliveryService{
    suspend fun getDelivery(deliveryId:Int): Result<DeliveryDTO?>
    suspend fun addDelivery(delivery:DeliveryDTO): Result<DeliveryDTO?>
}

class DeliveryService: ServiceUtil(), IDeliveryService {
    override suspend fun getDelivery(deliveryId: Int): Result<DeliveryDTO?> {
        val res = api.get(Deliveries.DeliveryId(deliveryId=deliveryId.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun addDelivery(delivery: DeliveryDTO): Result<DeliveryDTO?> {
        val res = api.post(Deliveries(),delivery)
        return complexResultWrapper(res)
    }
}