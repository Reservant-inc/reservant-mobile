package com.example.reservant_mobile.ui.constants

import com.example.reservant_mobile.ui.constants.RestaurantManagementArguments.MENU_ID
import com.example.reservant_mobile.ui.constants.RestaurantManagementArguments.RESTAURANT_ID

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
    val MENU_MANAGE = "${RestaurantManagementRoutes::class.simpleName}/manageMenu/{${RESTAURANT_ID}}"
    val MENU_ITEM_MANAGE = "${RestaurantManagementRoutes::class.simpleName}/manageMenuItem/{${MENU_ID}}"
    val EMPLOYEE_MANAGE = "${RestaurantManagementRoutes::class.simpleName}/employee/{${RESTAURANT_ID}}"
    
    fun getMenuItemManageRoute(menuId: Int) = "${RestaurantManagementRoutes::class.simpleName}/manageMenuItem/${menuId}"
    fun getMenuManageRoute(restaurantId: Int) = "${RestaurantManagementRoutes::class.simpleName}/manageMenu/${restaurantId}"
    fun getEmployeeManageRoute(restaurantId: Int) = "${RestaurantManagementRoutes::class.simpleName}/employee/${restaurantId}"
}