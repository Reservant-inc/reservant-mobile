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
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.scale
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(0.45f)) {
                    MyTimePickerDialog(
                        onTimeSelected = { time ->
                            reservationViewModel.startTime.value = time
                        },
                        modifier = Modifier
                            .scale(0.85f)
                    )
                }
                Icon(imageVector = Icons.Filled.Remove, contentDescription = "spacer")
                Box(modifier = Modifier.weight(0.45f)) {
                    MyTimePickerDialog(
                        onTimeSelected = { time ->
                            reservationViewModel.endTime.value = time
                        },
                        modifier = Modifier
                            .scale(0.85f)
                    )
                }
            }
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
            Text(text = stringResource(id = R.string.tip_label))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(5, 10, 15).forEach { percentage ->
                    Button(onClick = {
                        val totalCost = reservationViewModel.addedItems.sumOf { (menuItem, quantity) ->
                            (menuItem.price ?: 0.0) * quantity
                        }
                        reservationViewModel.tip = totalCost * percentage / 100.0
                    }) {
                        Text(text = "$percentage%")
                    }
                }
            }

            FormInput(
                inputText = if (reservationViewModel.tip == 0.0) "" else String.format("%.2f", reservationViewModel.tip),
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
            val formattedPrice = String.format("%.2f", totalCost)

            Text(
                text = "${stringResource(id = R.string.label_total_cost)}: $formattedPrice zł",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            // Submit Button
            ButtonComponent(
                onClick = {
                    navController.navigate(RestaurantRoutes.Summary(restaurantId = restaurantId))
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
                    Text(
                        text = "${stringResource(R.string.label_quantity)}: $itemCount",
                        style = MaterialTheme.typography.bodyLarge
                    )
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
