package com.example.reservant_mobile.ui.constants

data object Endpoints {
    const val BACKEND_URL="http://172.21.40.127:12038"
    const val FILE_UPLOADS="/uploads"
    const val LOGIN="/auth/login"
    const val LOGOUT="/auth/logout"
    const val LOGIN_UNIQUE="/auth/is-unique-login"
    const val REFRESH_ACCESS_TOKEN="/refresh-token"
    const val REGISTER_CUSTOMER="/auth/register-customer"
    const val REGISTER_CUSTOMER_SUPPORT_AGENT="/auth/register-customer-support-agent"
    const val REGISTER_RESTAURANT_EMPLOYEE="/auth/register-restaurant-employee"
    const val REGISTER_RESTAURANT_OWNER="/auth/register-restaurant-owner"
    const val MY_RESTAURANTS="/my-restaurants"
    fun MY_RESTAURANT(id: String) = "/my-restaurants/$id"
    fun RESTAURANT_MENUS(id: String) = "/my-restaurants/$id/menus"
    fun RESTAURANT_MENU(restaurantId: String, menuId:String) = "/my-restaurants/$restaurantId/menus/$menuId"
    fun RESTAURANT_MENU_ITEMS(id: String) = "/my-restaurants/$id/menu-items"
    fun RESTAURANT_MENU_ITEM(restaurantId: String, itemId:String) = "/my-restaurants/$restaurantId/menu-items/$itemId"


}
