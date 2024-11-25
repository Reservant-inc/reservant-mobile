package reservant_mobile.data.models.dtos

import com.example.reservant_mobile.R
import kotlinx.serialization.Serializable

@Serializable
data class TableDTO (
    val tableId: Int,
    val capacity: Int,
    val visitId: Int? = null,
    val status: TableStatus? = null
){
    @Serializable
    enum class TableStatus(val stringId: Int){
        Available(R.string.available_label),
        Taken(R.string.taken_label),
        VisitSoon(R.string.visit_soon_label)
    }
}