package reservant_mobile.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.ReservationViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@SuppressLint("DefaultLocale")
@Composable
fun OrderFormContent(
    navController: NavHostController,
    reservationViewModel: ReservationViewModel,
    restaurant: RestaurantDTO,            // <--- Teraz przyjmujemy CAŁĄ restaurację
    getMenuPhoto: suspend (String) -> Bitmap?,
) {
    var isTakeaway by remember { mutableStateOf(false) }
    var isDelivery by remember { mutableStateOf(false) }

    // Obliczenia pomocnicze do zaokrąglania godzin itd.
    val now = LocalTime.now()
    val nowFormatted = String.format("%02d:%02d", now.hour, now.minute)
    val isToday = reservationViewModel.visitDate.value == LocalDate.now().toString()

    // Funkcja, która zaokrągla LocalTime do najbliższej pełnej lub połówki
    fun roundToNearestHalfHour(time: LocalTime): LocalTime {
        val minute = time.minute
        return when {
            minute == 0 || minute == 30 -> time
            minute < 30 -> time.withMinute(30).withSecond(0).withNano(0)
            else -> time.withMinute(0).withSecond(0).withNano(0).plusHours(1)
        }
    }

    val nearestHalfHour = roundToNearestHalfHour(now)
    val nextHour = nearestHalfHour.plusHours(1)

    LazyColumn(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Spacer
        item {
            Spacer(modifier = androidx.compose.ui.Modifier.height(36.dp))
        }

        // Switch: "Na wynos?"
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.label_takeaway),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = isTakeaway,
                    onCheckedChange = {
                        isTakeaway = it
                        reservationViewModel.isTakeaway = it
                    }
                )
            }
        }

        // DatePicker
        item {
            MyDatePickerDialog(
                label = stringResource(id = R.string.label_reservation_date),  // przyjmuje String
                onDateChange = { selectedDate ->
                    Log.d("DEBUG", "onDateChange: $selectedDate")
                    // Zamiast tutaj walidować, wywołujemy metodę w ViewModelu
                    reservationViewModel.updateDate(selectedDate, restaurant)
                },
                startDate = LocalDate.now().toString(),
                allowFutureDates = true,
                allowPastDates = false,
                // Błędy pobieramy z VM
                isError = reservationViewModel.isDateError,
                errorText = reservationViewModel.dateErrorText
            )
        }

        // TimePicker start + end
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(0.45f)) {
                    MyTimePickerDialog(
                        initialTime = nearestHalfHour.format(DateTimeFormatter.ofPattern("HH:mm")),
                        onTimeSelected = { time ->
                            Log.d("DEBUG", "onTimeSelected: $time")
                            reservationViewModel.updateStartTime(time, restaurant)
                        },
                        modifier = Modifier.scale(0.85f),
                        onlyHalfHours = true,
                        minTime = if (isToday) nowFormatted else null,
                        // Błędy
                        isError = reservationViewModel.isStartTimeError,
                        errorText = reservationViewModel.startTimeErrorText
                    )
                }

                Icon(imageVector = Icons.Filled.Remove, contentDescription = "spacer")

                Box(modifier = Modifier.weight(0.45f)) {
                    MyTimePickerDialog(
                        initialTime = nextHour.format(DateTimeFormatter.ofPattern("HH:mm")),
                        onTimeSelected = { time ->
                            Log.d("DEBUG", "onTimeSelected: $time")
                            reservationViewModel.updateEndTime(time, restaurant)
                        },
                        modifier = androidx.compose.ui.Modifier.scale(0.85f),
                        onlyHalfHours = true,
                        minTime = reservationViewModel.startTime.value,
                        isError = reservationViewModel.isEndTimeError,
                        errorText = reservationViewModel.endTimeErrorText
                    )
                }
            }
        }

        // Koszyk
        item {
            Text(
                text = stringResource(R.string.label_my_basket),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (reservationViewModel.addedItems.isNotEmpty()) {
            items(reservationViewModel.addedItems) { item ->
                var menuPhoto by remember { mutableStateOf<Bitmap?>(null) }

                LaunchedEffect(item.first.photo) {
                    item.first.photo?.let { photo ->
                        menuPhoto = getMenuPhoto(photo)
                    }
                }
                CartItemCard(
                    item = item,
                    onIncreaseQuantity = { reservationViewModel.increaseItemQuantity(item) },
                    onDecreaseQuantity = { reservationViewModel.decreaseItemQuantity(item) },
                    onRemove = { reservationViewModel.removeItemFromCart(item) },
                    photo = menuPhoto
                )
            }
        } else {
            item {
                Text(text = stringResource(id = R.string.label_no_items_in_cart))
            }
        }

        // Liczba gości
        item {
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

        // Napiwek
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
                label = stringResource(id = R.string.tip_label),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            )
        }

        // Notatka
        item {
            FormInput(
                inputText = reservationViewModel.note.value,
                onValueChange = { reservationViewModel.note.value = it },
                label = stringResource(id = R.string.label_write_note),
            )
        }

        // Suma zamówienia
        item {
            val totalCost = reservationViewModel.addedItems.sumOf { (menuItem, quantity) ->
                (menuItem.price ?: 0.0) * quantity
            }
            val formattedPrice = String.format("%.2f", totalCost)

            Text(
                text = "${stringResource(id = R.string.label_order_cost)}: $formattedPrice zł",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    Log.d("DEBUG", "Submit clicked. isReservationValid=${reservationViewModel.isReservationValid()}")
                    if (reservationViewModel.isReservationValid()) {
                        navController.navigate(RestaurantRoutes.Summary(restaurantId = restaurant.restaurantId))
                    } else {
                        Log.d("DEBUG", "Validation failed: dateError=${reservationViewModel.isDateError}, startTimeError=${reservationViewModel.isStartTimeError}, endTimeError=${reservationViewModel.isEndTimeError}")
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.submit_order))
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: Pair<RestaurantMenuItemDTO, Int>,
    photo: Bitmap? = null,
    onInfoClick: () -> Unit = {},
    onIncreaseQuantity: () -> Unit = {},
    onDecreaseQuantity: () -> Unit = {},
    onRemove: () -> Unit = {}
) {
    val (menuItem, quantity) = item

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Górna część: nazwa, alternatywna nazwa, cena, procent alkoholu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = menuItem.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    menuItem.alternateName?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = stringResource(R.string.label_menu_price) + ": ${menuItem.price} zł",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    menuItem.alcoholPercentage?.let {
                        Text(
                            text = "Alcohol Percentage: ${it}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (photo != null) {
                    Image(
                        bitmap = photo.asImageBitmap(),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(start = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.unknown_image),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(start = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .fillMaxSize()
                    )
                }
            }

            // Dolna część: przyciski Info, Decrease, Increase, Remove
            Row(
                modifier = Modifier
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info Button
                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Decrease Quantity
                IconButton(
                    onClick = onDecreaseQuantity,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = stringResource(R.string.label_decrease_quantity),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Ilość w środku aby pokazać użytkownikowi aktualną liczbę
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Increase Quantity
                IconButton(
                    onClick = onIncreaseQuantity,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.label_increase_quantity),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Remove Entire Item
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.remove),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
