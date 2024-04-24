package com.example.reservant_mobile.data.models.dtos

data class EmploymentDTO(
    val restaurantId: Int,
    val isBackdoorEmployee: Boolean,
    val isHallEmployee: Boolean
)

data class EmployeeDTO(
    val id: String,
    val login: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val employments: List<EmploymentDTO>
)
