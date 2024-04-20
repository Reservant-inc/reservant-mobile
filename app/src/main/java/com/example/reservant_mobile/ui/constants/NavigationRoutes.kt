package com.example.reservant_mobile.ui.constants

data object MainRoutes {
    val ACTIVITY_HOME = "${MainRoutes::class.simpleName}/home"
    val ACTIVITY_LANDING = "${MainRoutes::class.simpleName}/landing"
    val ACTIVITY_LOGIN = "${MainRoutes::class.simpleName}/login"
    val ACTIVITY_REGISTER = "${MainRoutes::class.simpleName}/register"
}

data object RegisterRestaurantRoutes {
    val ACTIVITY_INPUTS = "${RegisterRestaurantRoutes::class.simpleName}/input"
    val ACTIVITY_FILES = "${RegisterRestaurantRoutes::class.simpleName}/files"
    val ACTIVITY_DESC = "${RegisterRestaurantRoutes::class.simpleName}/desc"
}

data object RestaurantManagementRoutes {
    val ACTIVITY_MANAGE = "${RestaurantManagementRoutes::class.simpleName}/manage"
}