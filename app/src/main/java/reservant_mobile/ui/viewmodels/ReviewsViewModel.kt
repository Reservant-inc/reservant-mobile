package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.ReviewDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.RestaurantService

class ReviewsViewModel(
    private val restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService()
) : ViewModel() {

    private val _reviewsFlow = MutableStateFlow<Flow<PagingData<ReviewDTO>>?>(null)
    val reviewsFlow: StateFlow<Flow<PagingData<ReviewDTO>>?> = _reviewsFlow

    var result: Result<ReviewDTO?> = Result(isError = false, value = null)
        private set

    var isSaving by mutableStateOf(false)
        private set

    init {
        fetchReviews()
    }

    fun fetchReviews() {
        viewModelScope.launch {
            val result: Result<Flow<PagingData<ReviewDTO>>?> = restaurantService.getRestaurantReviews(restaurantId)

            if (!result.isError) {
                _reviewsFlow.value = result.value?.cachedIn(viewModelScope)
            }
        }
    }

    suspend fun addReview(stars: Int, contents: String) {
        isSaving = true // Ustawienie flagi isSaving na true przed rozpoczęciem zapisu

        val newReview = ReviewDTO(
            stars = stars,
            contents = contents
        )

        val result = restaurantService.addRestaurantReview(restaurantId, newReview)

        this.result.isError = result.isError

        if (!result.isError) {
            fetchReviews()
        }

        isSaving = false
    }

    fun editReview(reviewId: Int, stars: Int, contents: String) {
        // TODO: Implementacja edycji opinii
    }

    fun deleteReview(reviewId: Int) {
        // TODO: Implementacja usuwania opinii
    }
}
