package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsDTO(
    val popularItems: List<PopularItemStat>? = null,
    val customerCount: List<CustomerStat>? = null,
    val revenue: List<RevenueStat>? = null,
    val reviews: List<ReviewsStat>? = null
){
    @Serializable
    data class PopularItemStat(
        val menuItem: RestaurantMenuItemDTO? = null,
        val amountOrdered: Int? = null
    )

    @Serializable
    data class CustomerStat(
        val date: String? = null,
        val customers: Int? = null
    )

    @Serializable
    data class RevenueStat(
        val date: String? = null,
        val revenue: Double? = null
    )

    @Serializable
    data class ReviewsStat(
        val date: String? = null,
        val count: Int? = null,
        val average: Double? = null
    )
}