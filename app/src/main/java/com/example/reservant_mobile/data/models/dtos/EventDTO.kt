package com.example.reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class EventDTO (
    val eventId: Int? = null ,
    val createdAt: String? = null,
    val description: String,
    val time: String,
    val mustJoinUntil: String,
    val creatorId: String? = null,
    val creatorFullName: String? = null,
    val restaurantId: Int,
    val restaurantName:String? = null,
    val visitId: Int? = null,
    /***
     * Interested user contains only userId, firstname, lastname
     */
    val interested: List<UserDTO>? = null,
    val numberInterested: Int? = null
)