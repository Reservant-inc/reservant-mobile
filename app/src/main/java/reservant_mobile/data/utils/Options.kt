package reservant_mobile.data.utils

enum class GetUsersFilter{
    NoFilter, FriendsOnly, StrangersOnly;
}

enum class GetRestaurantOrdersSort{
    DateAsc, DateDesc, CostAsc, CostDesc
}

enum class GetRestaurantReviewsSort{
    DateAsc, DateDesc, StarsAsc, StarsDesc
}