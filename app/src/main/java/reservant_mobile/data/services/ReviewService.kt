package reservant_mobile.data.services

import androidx.paging.PagingData
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import reservant_mobile.data.endpoints.Restaurants
import reservant_mobile.data.models.dtos.PageDTO
import reservant_mobile.data.models.dtos.ReviewDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.utils.GetRestaurantReviewsSort

interface IReviewService{
    suspend fun getReviews(restaurantId:Int, orderBy: GetRestaurantReviewsSort?): Result<Flow<PagingData<ReviewDTO>>?>

}

@OptIn(InternalSerializationApi::class)
class ReviewService: ServiceUtil(), IReviewService {

    override suspend fun getReviews(
        restaurantId:Int,
        orderBy: GetRestaurantReviewsSort?
    ): Result<Flow<PagingData<ReviewDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = {page, perPage -> api.get(
            Restaurants.Id.Reviews(
                parent = Restaurants.Id(restaurantId = restaurantId.toString()),
                orderBy = orderBy?.toString(),
                page = page,
                perPage = perPage
            )
        )}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(ReviewDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }
    
}