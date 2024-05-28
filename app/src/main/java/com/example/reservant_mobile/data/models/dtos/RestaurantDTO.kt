package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantDTO (
    val restaurantId: Int = Int.MIN_VALUE,
    val name: String,
    val restaurantType: String = "Restaurant",
    val nip: String = "",
    val address: String,
    val postalIndex: String = "",
    val city: String,
    val groupId: Int? = null,
    val groupName: String = "",
    val rentalContract: String? = null,
    val alcoholLicense: String? = null,
    val businessPermission: String? = null,
    val idCard: String = "",
    val tables:List<TableDTO> = emptyList(),
    val provideDelivery:Boolean = false,
    val logo:String? = null,
    val photos:List<String> = emptyList(),
    val isVerified:Boolean = false,
    val description:String = "",
    val reservationDeposit:Double? = null,
    val tags:List<String> = emptyList()
)
