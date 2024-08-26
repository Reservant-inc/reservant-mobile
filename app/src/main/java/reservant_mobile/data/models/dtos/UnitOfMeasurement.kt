package reservant_mobile.data.models.dtos

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
enum class UnitOfMeasurement {
    Gram,
    Liter,
    Unit,
}