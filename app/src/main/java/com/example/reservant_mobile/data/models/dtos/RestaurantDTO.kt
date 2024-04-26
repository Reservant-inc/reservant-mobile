package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantDTO (
    val id: Int = Int.MIN_VALUE,
    val name: String,
    val restaurantType: String,
    val nip: String = "",
    val address: String,
    val postalIndex: String = "",
    val city: String,
    val groupId: Int? = null,
    val groupName: String = "",
    val rentalContract: String? = "",
    val alcoholLicense: String? = "",
    val businessPermission: String = "",
    val idCard: String = "",
    val tables:List<TableDTO> = emptyList(),
    val provideDelivery:Boolean = false,
    val logo:String = "",
    val photos:List<String> = emptyList(),
    val isVerified:Boolean = false,
    val description:String = "",
    val tags:List<String> = emptyList()
)
