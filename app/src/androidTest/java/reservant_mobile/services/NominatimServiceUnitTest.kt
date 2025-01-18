package reservant_mobile.services

import kotlinx.coroutines.test.runTest
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import reservant_mobile.data.services.INominatumService
import reservant_mobile.data.services.NominatimService


class NominatimServiceUnitTest {
    private val service:INominatumService = NominatimService()

    @Test
    fun get_location_data_return_valid_data()= runTest{
        val res = service.getLocationData("Koszykowa", "Warszawa")
        assertThat(res.isError).isFalse()
        val obj = res.value!!
        assertThat(obj).isNotEmpty()
        assertThat(obj.first().lat).isGreaterThan(10)
        assertThat(obj.first().lon).isGreaterThan(10)
        assertThat(obj.first().displayName).contains(",")
    }

}