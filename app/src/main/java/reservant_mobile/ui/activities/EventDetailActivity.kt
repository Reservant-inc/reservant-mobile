package reservant_mobile.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.navigation.EventRoutes
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.EventViewModel
import reservant_mobile.ui.viewmodels.RestaurantDetailViewModel

@Composable
fun EventDetailActivity(
    navController: NavHostController,
    eventId: Int
) {
    val eventDetailVM = viewModel<EventViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                EventViewModel(eventId, false) as T
        }
    )

    var showEditDialog by remember { mutableStateOf(false) }
    val interested by rememberUpdatedState(newValue = eventDetailVM.interested.collectAsLazyPagingItems())

    if(eventDetailVM.isLoading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }else{
        Scaffold(
            topBar = {
                IconWithHeader(
                    text = eventDetailVM.event!!.name!!,
                    showBackButton = true,
                    onReturnClick = { navController.popBackStack() },
                    icon = Icons.Default.CalendarMonth,
                    actions = {
                        if (eventDetailVM.isEventOwner) {
                            IconButton(
                                onClick = {
                                    showEditDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Event")
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                item {
                    Image(
                        painter = painterResource(id = R.drawable.restaurant_photo),
                        contentDescription = "Event Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shadow(8.dp, RoundedCornerShape(8.dp))
                    )
                }

                if (showEditDialog) {
                    item {
                        EditEventDialog(
                            event = eventDetailVM.event!!,
                            onDismiss = { showEditDialog = false },
                            onSave = { updatedEvent ->
                                eventDetailVM.viewModelScope.launch {
                                    //eventDetailVM.updateEvent(updatedEvent)
                                }
                                showEditDialog = false
                            }
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Event Information",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Description Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = eventDetailVM.event!!.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val date = formatToDateTime(eventDetailVM.event!!.time, "dd MMMM yyyy")
                    val time = formatToDateTime(eventDetailVM.event!!.time, "HH:mm")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Time Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$time | $date",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            if(!eventDetailVM.isLoading && eventDetailVM.event!!.restaurant != null){
                                navController.navigate(
                                    RestaurantRoutes.Details(restaurantId = eventDetailVM.event!!.restaurant!!.restaurantId)
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Location Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Location: ${eventDetailVM.event!!.restaurant!!.name}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (eventDetailVM.isEventOwner) {
                    item {
                        Button(
                            onClick = { /* Cancel event action */ },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = "Cancel Event")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Cancel Event")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (eventDetailVM.isEventOwner && interested.itemCount != 0) {
                    item {
                        Text(text = "Join Requests", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(interested.itemCount) { index ->
                        val item = interested[index]
                        if (item != null) {
                            UserListItem(
                                user = EventDTO.Participant(
                                    userId = item.userId!!,
                                    firstName = item.firstName,
                                    lastName = item.lastName
                                ),
                                showButtons = true,
                                onApproveClick = { /* Approve user action */ },
                                onRejectClick = { /* Reject user action */ }
                            )
                            HorizontalDivider()
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item {
                    Text(text = "Participants", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(eventDetailVM.event!!.participants ?: emptyList()) { participant ->
                    if(eventDetailVM.event!!.participants!!.isEmpty())
                        Text(
                            text = "No one participates at this event."
                        )
                    UserListItem(user = participant)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun UserListItem(
    user: EventDTO.Participant,
    showButtons: Boolean = false,
    onApproveClick: (() -> Unit)? = null,
    onRejectClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.jd),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${user.firstName} ${user.lastName}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        if (showButtons) {
            Row {
                Button(
                    onClick = { onApproveClick?.invoke() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Approve")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve")
                }

                Button(
                    onClick = { onRejectClick?.invoke() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Reject")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject")
                }
            }
        }
    }
}

@Composable
fun EditEventDialog(
    event: EventDTO,
    onDismiss: () -> Unit,
    onSave: (EventDTO) -> Unit
) {

    // State variables for form fields
    var name by remember { mutableStateOf(event.name ?: "") }
    var description by remember { mutableStateOf(event.description ?: "") }
    var maxPeople by remember { mutableStateOf(event.maxPeople?.toString() ?: "") }
    var photo by remember { mutableStateOf(event.photo ?: "") }

    // Date and Time pickers for 'time' and 'mustJoinUntil'
    var eventDate by remember { mutableStateOf(event.time?.substring(0, 10) ?: "") }
    var eventTime by remember { mutableStateOf(event.time?.substring(11, 16) ?: "") }
    var joinUntilDate by remember { mutableStateOf(event.mustJoinUntil?.substring(0, 10) ?: "") }
    var joinUntilTime by remember { mutableStateOf(event.mustJoinUntil?.substring(11, 16) ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    // Validate and prepare the updated event data
                    val updatedEvent = event.copy(
                        name = name,
                        description = description,
                        maxPeople = maxPeople.toIntOrNull(),
                        photo = photo,
                        time = "${eventDate}T${eventTime}",
                        mustJoinUntil = "${joinUntilDate}T${joinUntilTime}"
                    )
                    onSave(updatedEvent)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Edit Event") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Event Name") },
                    isError = name.isBlank(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    isError = description.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = maxPeople,
                    onValueChange = { maxPeople = it },
                    label = { Text("Max People") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = maxPeople.toIntOrNull() == null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Date and Time pickers
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    DatePickerField(
//                        label = "Event Date",
//                        selectedDate = eventDate,
//                        onDateSelected = { eventDate = it }
//                    )
//                    TimePickerField(
//                        label = "Event Time",
//                        selectedTime = eventTime,
//                        onTimeSelected = { eventTime = it }
//                    )
//                }
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    DatePickerField(
//                        label = "Join Until Date",
//                        selectedDate = joinUntilDate,
//                        onDateSelected = { joinUntilDate = it }
//                    )
//                    TimePickerField(
//                        label = "Join Until Time",
//                        selectedTime = joinUntilTime,
//                        onTimeSelected = { joinUntilTime = it }
//                    )
//                }

                OutlinedTextField(
                    value = photo,
                    onValueChange = { photo = it },
                    label = { Text("Photo URL") },
                    isError = photo.isBlank(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }
    )
}