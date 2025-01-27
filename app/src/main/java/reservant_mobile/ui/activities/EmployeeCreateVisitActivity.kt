package reservant_mobile.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.navigation.NavController
import androidx.paging.LoadState
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.utils.DefaultResourceProvider
import reservant_mobile.ui.components.*
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.ReservationViewModel
import reservant_mobile.ui.viewmodels.SocialViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeCreateVisitActivity(
    restaurantId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: ReservationViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReservationViewModel(resourceProvider = DefaultResourceProvider(context)) as T
            }
        }
    )

    LaunchedEffect(restaurantId) {
        viewModel.getRestaurant(restaurantId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.employee_create_visit_title))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.label_back)
                        )
                    }
                }
            )
        }
    ) {
        viewModel.restaurant?.let { it1 ->
            EmployeeVisitFormContent(
                modifier = Modifier.padding(it),
                viewModel = viewModel,
                restaurant = it1
            )
        }
    }

    // Obsługa dialogów błędów i sukcesu
    viewModel.errorMessage?.let { err ->
        AlertDialog(
            onDismissRequest = { viewModel.errorMessage = null },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(err) },
            confirmButton = {
                ButtonComponent(
                    label = stringResource(R.string.ok),
                    onClick = { viewModel.errorMessage = null }
                )
            }
        )
    }
    viewModel.successMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.successMessage = null },
            title = { Text(stringResource(R.string.success_title)) },
            text = { Text(msg) },
            confirmButton = {
                ButtonComponent(
                    label = stringResource(R.string.ok),
                    onClick = {
                        viewModel.successMessage = null
                        navController.popBackStack()
                    }
                )
            }
        )
    }
}

@Composable
fun EmployeeVisitFormContent(
    modifier: Modifier = Modifier,
    viewModel: ReservationViewModel,       // Twój główny ViewModel do rezerwacji
    restaurant: RestaurantDTO
) {
    // ViewModel do wyszukiwania użytkowników (po imieniu)
    val socialViewModel = viewModel<SocialViewModel>()

    // Mapa ID -> UserDTO, by móc wyświetlać dane uczestników
    val participantsInfo = remember { mutableStateMapOf<String, UserDTO>() }

    val focusManager = LocalFocusManager.current

    // Czy popup do wyszukiwania jest otwarty
    var isUserPopupOpen by remember { mutableStateOf(false) }

    // Przykładowa logika obliczania godziny start/end
    val now = LocalTime.now()
    val nowFormatted = String.format("%02d:%02d", now.hour, now.minute)
    val isToday = viewModel.visitDate.value == LocalDate.now().toString()

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

    Column(modifier = modifier.padding(16.dp)) {

        // Takeaway
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.label_takeaway))
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = viewModel.isTakeaway,
                onCheckedChange = { viewModel.isTakeaway = it }
            )
        }

        // Wybór daty
        MyDatePickerDialog(
            label = stringResource(id = R.string.label_reservation_date),
            startStringValue = viewModel.visitDate.value,
            onDateChange = { selectedDate ->
                viewModel.updateDate(selectedDate, restaurant)
            },
            startDate = LocalDate.now().toString(),
            allowFutureDates = true,
            allowPastDates = false,
            isError = viewModel.isDateError,
            errorText = viewModel.dateErrorText
        )

        // Wyświetlamy godziny otwarcia (jeśli dostępne)
        val selectedDate = LocalDate.parse(viewModel.visitDate.value)
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

        Spacer(Modifier.height(8.dp))

        // Godziny start / end, ale tylko jeśli isTakeaway == false,
        // bo w kliencie taki jest warunek (startTime, endTime)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(0.45f)) {
                MyTimePickerDialog(
                    initialTime = nearestHalfHour.format(DateTimeFormatter.ofPattern("HH:mm")),
                    onTimeSelected = { time ->
                        viewModel.updateStartTime(time, restaurant)
                    },
                    modifier = Modifier.scale(0.85f),
                    onlyHalfHours = true,
                    minTime = if (isToday) nowFormatted else null,
                    isError = viewModel.isStartTimeError,
                    errorText = viewModel.startTimeErrorText
                )
            }
            if (!viewModel.isTakeaway) {
                Icon(imageVector = Icons.Filled.Remove, contentDescription = "spacer")

                Box(modifier = Modifier.weight(0.45f)) {
                    MyTimePickerDialog(
                        initialTime = nextHour.format(DateTimeFormatter.ofPattern("HH:mm")),
                        onTimeSelected = { time ->
                            viewModel.updateEndTime(time, restaurant)
                        },
                        modifier = Modifier.scale(0.85f),
                        onlyHalfHours = true,
                        minTime = viewModel.startTime.value,
                        isError = viewModel.isEndTimeError,
                        errorText = viewModel.endTimeErrorText
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Liczba gości
        Text(text = stringResource(R.string.label_number_of_guests))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (viewModel.totalGuests > 1) viewModel.totalGuests-- }) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = null)
            }
            Text(text = viewModel.totalGuests.toString())
            IconButton(onClick = { viewModel.totalGuests++ }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }

        Spacer(Modifier.height(16.dp))

        // SEKCJA UCZESTNIKÓW
        if(viewModel.participantIds.isNotEmpty()) {
            Text(
                text = stringResource(R.string.label_added_participants),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        viewModel.participantIds.forEach { userId ->
            val userDto = participantsInfo[userId]
            if (userDto != null) {
                UserCard(
                    firstName = userDto.firstName ?: "",
                    lastName = userDto.lastName ?: "",
                    getImage = {
                        userDto.photo?.let { photo -> viewModel.getPhoto(photo) }
                    },
                    isDeletable = true,
                    onRemove = {
                        viewModel.participantIds.remove(userId)
                        participantsInfo.remove(userId)
                    }
                )
            }
        }

         val maxParticipants = viewModel.totalGuests
        val currentCount = viewModel.participantIds.size
        val canAddMore = currentCount < maxParticipants

        if (!canAddMore) {
            Text(
                text = stringResource(R.string.info_reached_max_participants),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            ButtonComponent(
                label = stringResource(R.string.label_add_participant),
                onClick = {
                    focusManager.clearFocus()
                    socialViewModel.userQuery.value = ""
                    socialViewModel.getUsers()
                    isUserPopupOpen = true
                }
            )
        }

        Spacer(Modifier.height(24.dp))
        val label = stringResource(R.string.error_correct_time)
        ButtonComponent(
            label = stringResource(R.string.create_visit),
            onClick = {
                focusManager.clearFocus()

                // Minimalna walidacja
                if (viewModel.isDateError || viewModel.isStartTimeError || viewModel.isEndTimeError) {
                    viewModel.errorMessage = label
                    return@ButtonComponent
                }
                // Tworzymy wizytę
                viewModel.createGuestVisit(restaurant.restaurantId)
            }
        )
    }

    if (isUserPopupOpen) {
        UserSelectionPopup(
            onDismiss = { isUserPopupOpen = false },
            onUserSelected = { selectedUser ->
                val userId = selectedUser.userId
                if (userId != null && !viewModel.participantIds.contains(userId)) {
                    viewModel.participantIds.add(userId)
                    participantsInfo[userId] = selectedUser
                }
            },
            viewModel = socialViewModel
        )
    }
}


@Composable
fun UserSelectionPopup(
    onDismiss: () -> Unit,
    onUserSelected: (UserDTO) -> Unit,
    viewModel: SocialViewModel
) {
    val usersPaging = viewModel.users.collectAsLazyPagingItems()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Find Users") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Pole wyszukiwania
                OutlinedTextField(
                    value = viewModel.userQuery.value,
                    onValueChange = {
                        viewModel.userQuery.value = it
                        viewModel.getUsers()
                    },
                    placeholder = { Text("Search...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    trailingIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search"
                            )
                        }
                    },
                    singleLine = true
                )

                when {
                    viewModel.userQuery.value.isBlank() -> {
                        // Nic nie wpisano
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.empty_search))
                        }
                    }

                    usersPaging.loadState.refresh is LoadState.Loading -> {
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    usersPaging.loadState.refresh is LoadState.Error ||
                            usersPaging.itemCount == 0 -> {
                        // Brak wyników / błąd
                        MissingPage(
                            errorString = stringResource(R.string.error_users_not_found)
                        )
                    }

                    else -> {
                        // Mamy dane
                        LazyColumn {
                            items(usersPaging.itemCount) { idx ->
                                val user = usersPaging[idx]
                                user?.let { u ->
                                    UserCard(
                                        firstName = u.firstName,
                                        lastName = u.lastName,
                                        getImage = {
                                            u.photo?.let { photo ->
                                                viewModel.getPhoto(photo)
                                            }
                                        },
                                        onClick = {
                                            onUserSelected(u)
                                            onDismiss()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.label_close))
            }
        }
    )
}

