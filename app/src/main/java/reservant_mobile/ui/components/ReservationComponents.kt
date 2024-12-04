package reservant_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.ReservationViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun DeliveryContent(
    navController: NavHostController,
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
            inputText = viewModel.note.value,
            onValueChange = {
                viewModel.note.value = it // Update ViewModel
            },
            label = stringResource(R.string.label_write_note)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ButtonComponent(
            onClick = {
                navController.navigate(RestaurantRoutes.Summary)
            },
            label = stringResource(R.string.label_order_delivery),
        )
    }
}

@Composable
fun TakeawayContent(
    navController: NavHostController,
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
            inputText = viewModel.note.value,
            onValueChange = {
                viewModel.note.value = it
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
                navController.navigate(RestaurantRoutes.Summary)
            },
            label = stringResource(R.string.label_order_summary)
        )
    }
}


@Composable
fun DineInContent(
    navController: NavHostController,
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
            inputText = viewModel.note.value,
            onValueChange = {
                viewModel.note.value = it
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
                navController.navigate(RestaurantRoutes.Summary)
            },
            label = stringResource(id = R.string.label_order_summary)
        )
    }
}

@Composable
fun OrderFormContent(
    navController: NavHostController,
    reservationViewModel: ReservationViewModel,
    restaurantId: Int
) {
    var isTakeaway by remember { mutableStateOf(false) }
    var isDelivery by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Takeaway Switch
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(id = R.string.label_takeaway))
                Switch(
                    checked = isTakeaway,
                    onCheckedChange = {
                        isTakeaway = it
                        reservationViewModel.isTakeaway = it
                    }
                )
            }
        }

        item {
            // Delivery Switch
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(id = R.string.label_delivery))
                Switch(
                    checked = isDelivery,
                    onCheckedChange = {
                        isDelivery = it
                        // Handle delivery-specific logic
                    }
                )
            }
        }

        item {
            // Date Picker
            MyDatePickerDialog(
                label = { Text(stringResource(id = R.string.label_reservation_date)) },
                onDateChange = { selectedDate ->
                    reservationViewModel.visitDate.value = selectedDate
                },
                startDate = LocalDate.now().toString(),
                allowFutureDates = true
            )
        }

        item {
            // Time Picker for Start Time
            val startTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val initialStartTime = LocalTime.now().format(startTimeFormatter)
            MyTimePickerDialog(
                initialTime = initialStartTime,
                onTimeSelected = { time ->
                    reservationViewModel.visitDate.value = time
                }
            )
        }

        item {
            // Time Picker for End Time
            val startTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val initialEndTime = LocalTime.now().plusHours(1).format(startTimeFormatter) // For example, one hour later
            MyTimePickerDialog(
                initialTime = initialEndTime,
                onTimeSelected = { time ->
                    reservationViewModel.endTime.value = time
                }
            )
        }

        item {
            Text(
                text = stringResource(R.string.label_my_basket),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (reservationViewModel.addedItems.isNotEmpty()) {
            items(reservationViewModel.addedItems) { item ->
                OrderCard(
                    itemName = item.first.name ?: "",
                    itemCount = item.second,
                    itemCost = item.first.price ?: 0.0,
                    onIncreaseClick = {
                        reservationViewModel.increaseItemQuantity(item)
                    },
                    onDecreaseClick = {
                        reservationViewModel.decreaseItemQuantity(item)
                    }
                )
            }
        } else {
            item {
                Text(text = stringResource(id = R.string.label_no_items_in_cart))
            }
        }

        item {
            // Number of Guests
            Text(text = stringResource(id = R.string.label_number_of_guests))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        if (reservationViewModel.numberOfGuests > reservationViewModel.participantIds.size + 1)
                            reservationViewModel.numberOfGuests--
                    }
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null)
                }
                Text(text = reservationViewModel.numberOfGuests.toString())
                IconButton(
                    onClick = { reservationViewModel.numberOfGuests++ }
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }

        item {
            // Tip Input
            FormInput(
                inputText = reservationViewModel.tip.toString(),
                onValueChange = { reservationViewModel.tip = it.toDoubleOrNull() ?: 0.0 },
                label = stringResource(id = R.string.tip_label)
            )
        }

        item {
            // Note Input
            FormInput(
                inputText = reservationViewModel.note.value,
                onValueChange = { reservationViewModel.note.value = it },
                label = stringResource(id = R.string.label_write_note)
            )
        }

        item {
            // Promo Code Input
            FormInput(
                inputText = reservationViewModel.promoCode.value,
                onValueChange = { reservationViewModel.promoCode.value = it },
                label = stringResource(id = R.string.label_enter_promo_code)
            )
        }

        item {
            // Total Cost
            val totalCost = reservationViewModel.addedItems.sumOf { (menuItem, quantity) ->
                (menuItem.price ?: 0.0) * quantity
            }
            Text(
                text = "${stringResource(id = R.string.label_total_cost)}: $totalCost zł",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            // Submit Button
            ButtonComponent(
                onClick = {
                    reservationViewModel.createVisitAndOrder(
                        restaurantId = restaurantId,
                        isTakeaway = isTakeaway,
                        isDelivery = isDelivery
                    )
                    navController.navigate(RestaurantRoutes.Summary)
                },
                label = stringResource(id = R.string.submit_order)
            )
        }
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
