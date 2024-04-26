package com.example.reservant_mobile.ui.constants

data object AuthRoutes{
    val ACTIVITY_LANDING = "${AuthRoutes::class.simpleName}/landing"
    val ACTIVITY_LOGIN = "${AuthRoutes::class.simpleName}/login"
    val ACTIVITY_REGISTER = "${AuthRoutes::class.simpleName}/register"
}
data object MainRoutes {
    val ACTIVITY_HOME = "${MainRoutes::class.simpleName}/home"

    val ACTIVITY_PROFILE = "${MainRoutes::class.simpleName}/profile"
}

data object RegisterRestaurantRoutes {
    val ACTIVITY_REGISTER_RESTAURANT = "${RegisterRestaurantRoutes::class.simpleName}/register-restaurant"
    val ACTIVITY_INPUTS = "${RegisterRestaurantRoutes::class.simpleName}/input"
    val ACTIVITY_FILES = "${RegisterRestaurantRoutes::class.simpleName}/files"
    val ACTIVITY_DESC = "${RegisterRestaurantRoutes::class.simpleName}/desc"
}

data object RestaurantManagementRoutes {
    val ACTIVITY_MANAGE = "${RestaurantManagementRoutes::class.simpleName}/manage"
}