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

enum class GetVisitsSort{
    DateAsc, DateDesc
}

enum class GetIngredientsSort{
    NameAsc, NameDesc, AmountAsc, AmountDesc
}

enum class GetDeliveriesSort{
    OrderTimeAsc, OrderTimeDesc, DeliveredTimeAsc, DeliveredTimeDesc
}