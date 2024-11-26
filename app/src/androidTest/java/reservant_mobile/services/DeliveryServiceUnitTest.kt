package reservant_mobile.services

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.data.services.DeliveryService
import reservant_mobile.data.services.IDeliveryService

class DeliveryServiceUnitTest: ServiceTest() {
    private val ser: IDeliveryService = DeliveryService()
    private val delivery = DeliveryDTO(
        restaurantId = 1,
        ingredients = listOf(DeliveryDTO.DeliveryIngredientDTO(
            deliveryId = 1,
            ingredientId = 1,
            amountOrdered = 1.0,
            amountDelivered = 1.0,
            expiryDate = "2024-09-02T18:01:57.229Z",
            storeName = "Test Store"
        )   )
    )


    @Before
    fun setupData() = runBlocking {
        loginUser()
    }

    @Test
    fun get_delivery_return_not_null()= runTest{
        assertThat(ser.getDelivery(1).value).isNotNull()
    }

    @Test
    fun add_delivery_return_not_null()= runTest{
        assertThat(ser.addDelivery(delivery).value).isNotNull()
    }

    @Test
    fun confirm_return_not_null()= runTest{
        assertThat(ser.confirmDelivery(1).value).isNotNull()
    }

    @Test
    fun mark_canceled_return_not_null()= runTest{
        assertThat(ser.markDeliveryCanceled(1).value).isNotNull()
    }
}