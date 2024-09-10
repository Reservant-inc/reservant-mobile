package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/reviews")
class Reviews {
    @Resource("{reviewId}")
    class ReviewId(val parent: Reviews = Reviews(), val reviewId: String)
}