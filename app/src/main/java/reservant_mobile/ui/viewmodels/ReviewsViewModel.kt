package reservant_mobile.ui.viewmodels

import android.graphics.Bitmap
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
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.ReviewDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.models.dtos.UserSummaryDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.FileService
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.services.UserService

class ReviewsViewModel(
    private val restaurantId: Int,
    private val restaurantService: IRestaurantService = RestaurantService(),
    private val fileService: FileService = FileService(),
    private val userService: IUserService = UserService()
) : ViewModel() {

    var restaurant: RestaurantDTO? by mutableStateOf(null)

    private val _reviewsFlow = MutableStateFlow<Flow<PagingData<ReviewDTO>>?>(null)
    val reviewsFlow: StateFlow<Flow<PagingData<ReviewDTO>>?> = _reviewsFlow

    private val _review =
        MutableStateFlow<Result<ReviewDTO?>>(Result(isError = false, value = null))
    val review: StateFlow<Result<ReviewDTO?>> = _review

    var result: Result<ReviewDTO?> = Result(isError = false, value = null)
        private set

    var isSaving by mutableStateOf(false)
        private set

    init {
        fetchReviews()
    }

    suspend fun getPhoto(photoStr: String): Bitmap? {
        val result = fileService.getImage(photoStr)
        if (!result.isError){
            return  result.value!!
        }
        return null
    }

    suspend fun getUser(userId: String): UserSummaryDTO? {
        val result = userService.getUserSimpleInfo(userId)

        if(!result.isError){
            return result.value
        }else{
            throw Exception()
        }
    }

    fun fetchReviews() {
        viewModelScope.launch {
            val result: Result<Flow<PagingData<ReviewDTO>>?> =
                restaurantService.getRestaurantReviews(restaurantId)

            if (!result.isError) {
                _reviewsFlow.value = result.value?.cachedIn(viewModelScope)
            }
        }
    }

    fun fetchReview(reviewId: Int) {
        viewModelScope.launch {
            val result: Result<ReviewDTO?> = restaurantService.getRestaurantReview(reviewId)

            if (!result.isError) {
                _review.value = result
            } else {
                _review.value = Result(isError = true, value = null)
            }
        }
    }

    suspend fun addReview(stars: Int, contents: String) {
        isSaving = true

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
        viewModelScope.launch {
            isSaving = true

            val updatedReview = ReviewDTO(
                stars = stars,
                contents = contents
            )

            val result = restaurantService.editRestaurantReview(reviewId, updatedReview)

            this@ReviewsViewModel.result.isError = result.isError

            if (!result.isError) {
                fetchReviews()
            }

            isSaving = false
        }
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch {
            isSaving = true

            val result = restaurantService.deleteRestaurantReview(reviewId)

            this@ReviewsViewModel.result.isError = result.isError

            if (!result.isError) {
                fetchReviews()
            }

            isSaving = false
        }
    }

    fun postReply(reviewId: Int, replyContent: String) {
        viewModelScope.launch {
            isSaving = true

            val result = restaurantService.addRestaurantResponse(reviewId, replyContent)

            this@ReviewsViewModel.result.isError = result.isError

            if (!result.isError) {
                fetchReviews()
            }

            isSaving = false
        }
    }
}
