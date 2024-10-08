package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class EmploymentDTO(
    val employmentId: Int? = null,
    val isBackdoorEmployee: Boolean? = null,
    val isHallEmployee: Boolean? = null,
    val restaurant: RestaurantDTO? = null,
    /***
     * Date in 'yyyy-MM-dd' format
     */
    val dateUntill: String? = null,
    /***
     * Date in 'yyyy-MM-dd' format
     */
    val dateFrom: String? = null
)