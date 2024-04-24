package com.example.reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reservant_mobile.data.models.dtos.EmployeeDTO
import com.example.reservant_mobile.data.models.dtos.EmploymentDTO
import com.example.reservant_mobile.ui.components.EmployeeCard

val employee = EmployeeDTO(
    id = "001",
    login = "johndoe",
    firstName = "John",
    lastName = "Doe",
    phoneNumber = "+1234567890",
    employments = listOf(
        EmploymentDTO(
            restaurantId = 101,
            isBackdoorEmployee = true,
            isHallEmployee = false
        ),
        EmploymentDTO(
            restaurantId = 102,
            isBackdoorEmployee = false,
            isHallEmployee = true
        )
    )
)
@Composable
fun EmployeeManagementActivity() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmployeeCard(employee = employee)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmployee() {
    EmployeeManagementActivity()
}