package com.example.reservant_mobile.ui.constants

data object Endpoints {

    const val BACKEND_URL="http://172.21.40.127:12038"
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
    fun MY_RESTAURANT_EMPLOYEES(id: String) = "/my-restaurants/$id/employees"
    fun MOVE_RESTAURANT_TO_GROUP(id: String) = "/my-restaurants/$id/move-to-group"

    const val MY_RESTAURANT_GROUPS="/my-restaurant-groups"
    fun MY_RESTAURANT_GROUP(id: String) = "/my-restaurant-groups/$id"

    const val FILE_UPLOADS="/uploads"

}
