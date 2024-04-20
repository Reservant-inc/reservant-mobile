package com.example.reservant_mobile.ui.constants

data object MainRoutes {
    val ACTIVITY_HOME = "${MainRoutes::class.simpleName}/home"
    val ACTIVITY_LANDING = "${MainRoutes::class.simpleName}/landing"
    val ACTIVITY_LOGIN = "${MainRoutes::class.simpleName}/login"
    val ACTIVITY_REGISTER = "${MainRoutes::class.simpleName}/register"
    val ACTIVITY_REGISTER_RESTAURANT = "${MainRoutes::class.simpleName}/register-restaurant"
    val ACTIVITY_PROFILE = "${MainRoutes::class.simpleName}/profile"
}

data object RegisterRestaurantRoutes {
    val ACTIVITY_INPUTS = "${RegisterRestaurantRoutes::class.simpleName}/input"
    val ACTIVITY_FILES = "${RegisterRestaurantRoutes::class.simpleName}/files"
    val ACTIVITY_DESC = "${RegisterRestaurantRoutes::class.simpleName}/desc"
}