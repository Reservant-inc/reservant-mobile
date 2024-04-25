package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantDTO (
    val id: Int? = null,
    val name: String,
    val restaurantType: String,
    val nip: String? = null,
    val address: String,
    val postalIndex: String? = null,
    val city: String,
    val groupId: Int? = null,
    val groupName: String? = null,
    val rentalContract: String? = null,
    val alcoholLicense: String? = null,
    val businessPermission: String? = null,
    val idCard: String? = null,
    val tables:List<TableDTO>? = null,
    val provideDelivery:Boolean? = null,
    val logo:String? = null,
    val photos:List<String>? = null,
    val isVerified:Boolean? = null,
    val description:String? = null,
    val tags:List<String>? = null
)
