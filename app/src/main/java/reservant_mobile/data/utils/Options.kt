package reservant_mobile.data.utils

import com.example.reservant_mobile.R

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

enum class GetUserEventsSort{
    DateCreatedAsc, DateCreatedDesc, DateAsc, DateDesc
}

enum class GetUserEventsCategory(val stringId: Int){
    CreatedBy(R.string.label_event_category_created),
    ParticipateIn(R.string.label_event_category_participated),
    InterestedIn(R.string.label_event_category_interested)
}