package com.example.reservant_mobile.ui.constants

data object Endpoints {
    const val URL_PATH = "http://172.21.40.127:12038"

    const val LOGIN="${URL_PATH}/auth/login"
    const val LOGOUT="${URL_PATH}/auth/logout"
    const val REGISTER_CUSTOMER="${URL_PATH}/auth/register-customer"
    const val REGISTER_CUSTOMER_SUPPORT_AGENT="${URL_PATH}/auth/register-customer-support-agent"
    const val REGISTER_RESTAURANT_EMPLOYEE="${URL_PATH}/auth/register-restaurant-employee"
    const val REGISTER_RESTAURANT_OWNER="${URL_PATH}/auth/register-restaurant-owner"
}
