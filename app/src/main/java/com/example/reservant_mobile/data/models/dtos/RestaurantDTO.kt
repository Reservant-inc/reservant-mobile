package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantDTO (
    val id: Int,
    val name: String,
    val restaurantType: String,
    val nip: String = "",
    val address: String,
    val postalIndex: String = "",
    val city: String,
    val groupId: Int,
    val groupName: String = "",
    val rentalContract: String = "",
    val alcoholLicense: String = "",
    val businessPermission: String = "",
    val idCard: String = "",
    val tables:List<TableDTO> = emptyList(),
    val provideDelivery:Boolean,
    val logo:String,
    val photos:List<String> = emptyList(),
    val description:String,
    val tags:List<String>,
    val isVerified:Boolean
)