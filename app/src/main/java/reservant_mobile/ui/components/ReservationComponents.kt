package reservant_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.ui.viewmodels.ReservationViewModel
import java.time.LocalDate


@Composable
fun DeliveryContent(
    viewModel: ReservationViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.label_delivery),
            style = MaterialTheme.typography.headlineSmall
        )

        FormInput(
            inputText = viewModel.deliveryAddress.value,
            onValueChange = {
                viewModel.deliveryAddress.value = it // Update ViewModel
            },
            label = stringResource(R.string.label_delivery_address)
        )
        
        Spacer(modifier = Modifier.padding(8.dp))

        Text(
            text = stringResource(R.string.label_my_basket),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                OrderCard(
                    itemName = "Danie1",
                    itemCount = 1,
                    itemCost = 30.0,
                    onIncreaseClick = { /* TODO: Handle increase item count */ },
                    onDecreaseClick = { /* TODO: Handle decrease item count */ }
                )
            }
        }

        FormInput(
            inputText = viewModel.deliveryNote.value,
            onValueChange = {
                viewModel.deliveryNote.value = it // Update ViewModel
            },
            label = stringResource(R.string.label_write_note)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ButtonComponent(
            onClick = {
                viewModel.viewModelScope.launch {
                    viewModel.addDelivery()
                    if (viewModel.deliveryResult.value.isError) {
                        // Handle error, e.g., show a toast
                    } else {
                        // Handle success, e.g., navigate to another screen or show confirmation
                    }
                }
            },
            label = stringResource(R.string.label_order_delivery),
        )
    }
}

@Composable
fun TakeawayContent(
    viewModel: ReservationViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, end = 8.dp, start = 8.dp, bottom = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.label_my_basket),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                OrderCard(
                    itemName = "Danie1",
                    itemCount = 1,
                    itemCost = 30.0,
                    onIncreaseClick = { /* TODO: Handle increase item count */ },
                    onDecreaseClick = { /* TODO: Handle decrease item count */ }
                )
            }
        }

        FormInput(
            inputText = viewModel.orderNote.value,
            onValueChange = {
                viewModel.orderNote.value = it
            },
            label = stringResource(R.string.label_write_note),
            isError = false
        )

        FormInput(
            inputText = viewModel.promoCode.value,
            onValueChange = {
                viewModel.promoCode.value = it
            },
            label = stringResource(R.string.label_enter_promo_code),
            isError = false
        )

        Row{
            Text(
                text = stringResource(R.string.label_total_amount),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = " ${viewModel.orderCost} zł",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        ButtonComponent(
            onClick = {
                viewModel.viewModelScope.launch {
                    viewModel.createOrder()
                    if (viewModel.orderResult.value.isError) {
                        // Handle error, e.g., show a toast
                    } else {
                        // Handle success
                    }
                }
            },
            label = stringResource(R.string.label_order_summary)
        )
    }
}


@Composable
fun DineInContent(
    viewModel: ReservationViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_reservation),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
        )

        // Date Picker Dialog
        MyDatePickerDialog(
            label = { Text(stringResource(id = R.string.label_reservation_date)) },
            onDateChange = { selectedDate ->
                // Handle date change
                viewModel.visitDate.value = selectedDate
            },
            startDate = LocalDate.now().toString(),
            allowFutureDates = true
        )

        // Number of Guests
        Text(
            text = stringResource(id = R.string.label_number_of_guests),
            style = MaterialTheme.typography.bodyLarge
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { if (viewModel.seats > 1) viewModel.seats-- },
                color = MaterialTheme.colorScheme.primary,
                enabled = viewModel.seats > 1,
                icon = "-"
            )
            Text(
                text = viewModel.seats.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = { if (viewModel.seats < 10) viewModel.seats++ },
                color = MaterialTheme.colorScheme.primary,
                enabled = viewModel.seats < 10,
                icon = "+"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Order Summary Header
        Text(
            text = stringResource(id = R.string.label_order_summary),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                OrderCard(
                    itemName = "Danie1",
                    itemCount = 1,
                    itemCost = 30.0,
                    onIncreaseClick = { /* TODO: Handle increase item count */ },
                    onDecreaseClick = { /* TODO: Handle decrease item count */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comment Input
        FormInput(
            inputText = viewModel.visitNote.value,
            onValueChange = {
                viewModel.visitNote.value = it
            },
            label = stringResource(id = R.string.label_write_note),
            isError = false
        )

        // Promo Code Input
        FormInput(
            inputText = viewModel.promoCode.value,
            onValueChange = { viewModel.promoCode.value = it },
            label = stringResource(id = R.string.label_enter_promo_code),
            isError = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total Cost Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.label_total_cost),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${viewModel.orderCost} zł", // Example total cost
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        ButtonComponent(
            onClick = {
                viewModel.viewModelScope.launch {
                    viewModel.createVisit()
                    if (viewModel.visitResult.value.isError) {
                        // Handle error, e.g., show a toast
                    } else {
                        // Handle success
                    }
                }
            },
            label = stringResource(id = R.string.label_order_summary)
        )
    }
}

@Composable
fun OrderCard(
    itemName: String,
    itemCount: Int,
    itemCost: Double,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column {
            // Row for item details and quantity controls
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = itemName, style = MaterialTheme.typography.bodyLarge)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${stringResource(R.string.label_quantity)}: $itemCount", style = MaterialTheme.typography.bodyLarge)
                    IconButton(
                        onClick = onDecreaseClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = stringResource(R.string.label_decrease_quantity)
                        )
                    }
                    IconButton(
                        onClick = onIncreaseClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.label_increase_quantity)
                        )
                    }
                }
            }
            // Text for item cost
            Text(
                text = "${stringResource(R.string.label_cost)}: ${itemCost}zł",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
