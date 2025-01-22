package reservant_mobile.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.FriendRequestDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.ReservationViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@SuppressLint("DefaultLocale")
@Composable
fun OrderFormContent(
    navController: NavHostController,
    reservationViewModel: ReservationViewModel,
    restaurant: RestaurantDTO,
    getMenuPhoto: suspend (String) -> Bitmap?,
    isReservation: Boolean
) {
    val isTakeawayTag by remember { mutableStateOf("Takeaway" in restaurant.tags) }
    val isOnSiteTag by remember { mutableStateOf("OnSite" in restaurant.tags) }
    var isTakeaway by remember { mutableStateOf(false) }
    var isDelivery by remember { mutableStateOf(false) }

    var isAddFriendPopupOpen by remember { mutableStateOf(false) }
    val participantsInfo = remember { mutableStateMapOf<String, FriendRequestDTO>() }


    val now = LocalTime.now()
    val nowFormatted = String.format("%02d:%02d", now.hour, now.minute)
    val isToday = reservationViewModel.visitDate.value == LocalDate.now().toString()

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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(36.dp)) }
        if (isTakeawayTag && !isReservation) {
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
        }

        item {
            MyDatePickerDialog(
                label = stringResource(id = R.string.label_reservation_date),
                startStringValue = reservationViewModel.visitDate.value,
                onDateChange = { selectedDate ->
                    reservationViewModel.updateDate(selectedDate, restaurant)
                },
                startDate = LocalDate.now().toString(),
                allowFutureDates = true,
                allowPastDates = false,
                isError = reservationViewModel.isDateError,
                errorText = reservationViewModel.dateErrorText
            )

            val selectedDate = LocalDate.parse(reservationViewModel.visitDate.value)
            val dayIndex = selectedDate.dayOfWeek.value - 1
            val dayHours = restaurant.openingHours?.getOrNull(dayIndex)
            if (dayHours != null) {
                Text(
                    text = stringResource(
                        id = R.string.opening_hours,
                        dayHours.from ?: "-",
                        dayHours.until ?: "-"
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                Text(
                    text = stringResource(id = R.string.restaurant_closed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

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
                            reservationViewModel.updateStartTime(time, restaurant)
                        },
                        modifier = Modifier.scale(0.85f),
                        onlyHalfHours = true,
                        minTime = if (isToday) nowFormatted else null,
                        isError = reservationViewModel.isStartTimeError,
                        errorText = reservationViewModel.startTimeErrorText
                    )
                }
                if (!isTakeaway) {
                    Icon(imageVector = Icons.Filled.Remove, contentDescription = "spacer")

                    Box(modifier = Modifier.weight(0.45f)) {
                        MyTimePickerDialog(
                            initialTime = nextHour.format(DateTimeFormatter.ofPattern("HH:mm")),
                            onTimeSelected = { time ->
                                reservationViewModel.updateEndTime(time, restaurant)
                            },
                            modifier = Modifier.scale(0.85f),
                            onlyHalfHours = true,
                            minTime = reservationViewModel.startTime.value,
                            isError = reservationViewModel.isEndTimeError,
                            errorText = reservationViewModel.endTimeErrorText
                        )
                    }
                }
            }
        }

        if (!isReservation) {
            item {
                Text(
                    text = stringResource(id = R.string.label_my_basket),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        if (!isReservation) {
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
                item { Text(text = stringResource(id = R.string.label_no_items_in_cart)) }
            }
        }

        if (!isTakeaway) {
            item {
                Text(text = stringResource(id = R.string.label_number_of_guests))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            if (reservationViewModel.totalGuests > reservationViewModel.participantIds.size + 1)
                                reservationViewModel.totalGuests--
                        }
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null)
                    }
                    Text(text = reservationViewModel.totalGuests.toString())
                    IconButton(onClick = { reservationViewModel.totalGuests++ }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }
            // PARTICIPANTS SECTION
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.label_added_participants),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Show each participant
                reservationViewModel.participantIds.forEach { friendId ->
                    val friendDto by remember(participantsInfo) { derivedStateOf { participantsInfo[friendId] } }
                    if (friendDto != null) {
                        UserCard(
                            firstName = friendDto!!.otherUser?.firstName,
                            lastName = friendDto!!.otherUser?.lastName,
                            getImage = {
                                friendDto!!.otherUser?.photo?.let { photo ->
                                    reservationViewModel.getPhoto(photo)
                                }
                            },
                            isDeletable = true,
                            onRemove = {
                                reservationViewModel.participantIds.remove(friendId)
                                participantsInfo.remove(friendId)
                            }
                        )
                    }
                }

                val maxParticipants by remember {
                    mutableStateOf(reservationViewModel.totalGuests - 1)
                }

                val currentCount by remember {
                    derivedStateOf { reservationViewModel.participantIds.size }
                }

                val canAddMore by remember {
                    derivedStateOf { reservationViewModel.totalGuests > 1 && currentCount < maxParticipants }
                }

                if (!canAddMore) {
                    Text(
                        text = when {
                            reservationViewModel.totalGuests <= 1 ->
                                stringResource(R.string.info_need_at_least_two_guests)

                            else ->
                                stringResource(R.string.info_reached_max_participants)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    ButtonComponent(
                        onClick = {
                            isAddFriendPopupOpen = true
                            reservationViewModel.loadFriendsPaging()
                        },
                        label = stringResource(R.string.label_add_participant)
                    )
                }
            }

        }

        if (!isTakeaway) {
            item {
                Text(text = stringResource(id = R.string.tip_label))

                if (!isReservation) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(5, 10, 15).forEach { percentage ->
                            Button(onClick = {
                                val totalCost =
                                    reservationViewModel.addedItems.sumOf { (menuItem, quantity) ->
                                        (menuItem.price ?: 0.0) * quantity
                                    }
                                reservationViewModel.tip = totalCost * percentage / 100.0
                            }) {
                                Text(text = "$percentage%")
                            }
                        }
                    }
                }

                FormInput(
                    inputText = if (reservationViewModel.tip == 0.0) "" else String.format(
                        "%.2f",
                        reservationViewModel.tip
                    ),
                    onValueChange = { reservationViewModel.tip = it.toDoubleOrNull() ?: 0.0 },
                    label = stringResource(id = R.string.tip_label),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    optional = true,
                    isError = reservationViewModel.isTipError(),
                    errorText = stringResource(id = R.string.error_tip)
                )
            }
        }

        if (!isReservation) {
            item {
                FormInput(
                    inputText = reservationViewModel.note.value,
                    onValueChange = { reservationViewModel.note.value = it },
                    label = stringResource(id = R.string.label_write_note),
                    optional = true
                )
            }

            item {
                val totalCost = reservationViewModel.addedItems.sumOf { (menuItem, quantity) ->
                    (menuItem.price ?: 0.0) * quantity
                } + reservationViewModel.tip
                val formattedPrice = String.format("%.2f", totalCost)

                Text(
                    text = "${stringResource(id = R.string.label_order_cost)}: $formattedPrice zł",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            ButtonComponent(
                label = stringResource(
                    id = if (isReservation) R.string.submit_reservation else R.string.submit_order
                ),
                onClick = {
                    if (reservationViewModel.isReservationValid(isReservation = isReservation)) {
                        navController.navigate(
                            RestaurantRoutes.Summary(
                                restaurantId = restaurant.restaurantId,
                                isReservation = isReservation
                            )
                        )
                    }
                })
        }
    }
    if (isAddFriendPopupOpen) {
        AddFriendPopupPaging(
            onDismiss = { isAddFriendPopupOpen = false },
            reservationViewModel = reservationViewModel,
            onAddFriend = { friendId, friendDto ->
                participantsInfo[friendId] = friendDto
                if (!reservationViewModel.participantIds.contains(friendId)) {
                    reservationViewModel.participantIds.add(friendId)
                }
                isAddFriendPopupOpen = false
            }
        )
    }
}

@Composable
fun AddFriendPopupPaging(
    onDismiss: () -> Unit,
    reservationViewModel: ReservationViewModel,
    onAddFriend: (String, FriendRequestDTO) -> Unit
) {
    // Collect the paged flow
    val lazyFriends = reservationViewModel.friendsFlow?.collectAsLazyPagingItems()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.label_select_friends)) },
        text = {
            if (lazyFriends == null) {
                // Not loaded => show spinner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Check load states
                val loadState = lazyFriends.loadState.refresh
                when {
                    loadState is LoadState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    loadState is LoadState.Error -> {
                        Text(
                            text = stringResource(R.string.error_loading_data),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    else -> {
                        val itemCount by remember { derivedStateOf { lazyFriends.itemCount } }
                        if (itemCount == 0) {
                            Text(
                                text = stringResource(R.string.label_no_friends),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        } else {
                            LazyColumn(modifier = Modifier.height(300.dp)) {
                                items(itemCount) { index ->
                                    val friend = lazyFriends[index]
                                    if (friend != null) {
                                        UserCard(
                                            firstName = friend.otherUser?.firstName,
                                            lastName = friend.otherUser?.lastName,
                                            getImage = {
                                                friend.otherUser?.photo?.let { photo ->
                                                    reservationViewModel.getPhoto(photo)
                                                }
                                            },
                                            onClick = {
                                                friend.otherUser?.userId?.let { uid ->
                                                    onAddFriend(uid, friend)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.label_close))
            }
        },
        dismissButton = {}
    )
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
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

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

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