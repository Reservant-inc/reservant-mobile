package reservant_mobile.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.IconWithHeader

@Composable
fun EventDetailActivity(
    navController: NavHostController,
    eventId: Int
) {
    val event = remember { mutableStateOf(mockEventData()) }
    val isOwner = true

    Scaffold(
        topBar = {
            IconWithHeader(
                text = event.value.name ?: "Event",
                showBackButton = true,
                onReturnClick = { navController.popBackStack() },
                icon = Icons.Default.CalendarMonth,
                actions = {
                    if (isOwner) {
                        IconButton(onClick = { /* Navigate to edit event */ }) {
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
                        text = event.value.description ?: "",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val date = formatToDateTime(event.value.time, "dd MMMM yyyy")
                val time = formatToDateTime(event.value.time, "HH:mm")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Date and Time: $date at $time",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        // Navigate to restaurant details
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
                        text = "Location: ${event.value.restaurant?.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isOwner) {
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

            val joinRequests = listOf(
                EventDTO.Participant(userId = "1", firstName = "Charlie", lastName = "Brown"),
                EventDTO.Participant(userId = "2", firstName = "Dana", lastName = "White")
            )

            if (isOwner) {
                item {
                    Text(text = "Join Requests", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(joinRequests) { user ->
                    UserListItem(
                        user = user,
                        showButtons = true,
                        onApproveClick = { /* Approve user action */ },
                        onRejectClick = { /* Reject user action */ }
                    )
                    Divider()
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                Text(text = "Participants", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(event.value.participants ?: emptyList()) { participant ->
                UserListItem(user = participant)
                HorizontalDivider()
            }
        }
    }
}

fun mockEventData(): EventDTO {
    return EventDTO(
        eventId = 1,
        name = "Restaurant Pool Party",
        description = "Join us for a fun pool party with great food and music!",
        time = "2023-12-31T20:00:00",
        mustJoinUntil = "2023-12-30T23:59:00",
        restaurant = RestaurantDTO(
            restaurantId = 1,
            name = "Sample Restaurant",
            address = "123 Sample Street",
            city = "Sample City"
        ),
        participants = listOf(
            EventDTO.Participant(userId = "1", firstName = "Alice", lastName = "Smith"),
            EventDTO.Participant(userId = "2", firstName = "Bob", lastName = "Johnson")
        ),
    )
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
            .fillMaxWidth() // Upewniamy się, że Row zajmuje całą szerokość
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
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
