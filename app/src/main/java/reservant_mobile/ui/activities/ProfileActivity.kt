package reservant_mobile.ui.activities

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.FriendRequestDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.models.dtos.UserSummaryDTO.FriendStatus
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.EventCard
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.viewmodels.ProfileViewModel

@Composable
fun ProfileActivity(navController: NavHostController, userId: String) {
    val profileViewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProfileViewModel(profileUserId = userId) as T
        }
    )

    val friendsFlow by profileViewModel.friendsFlow.collectAsState()
    val friendsPagingItems = friendsFlow?.collectAsLazyPagingItems()

    val eventsFlow by profileViewModel.eventsFlow.collectAsState()
    val eventPagingItems = eventsFlow?.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            IconWithHeader(
                icon = Icons.Default.Person,
                text = if (profileViewModel.isCurrentUser)
                    stringResource(R.string.label_my_profile)
                else
                    stringResource(R.string.label_profile),
                showBackButton = true,
                onReturnClick = { navController.popBackStack() },
                actions = if (profileViewModel.isCurrentUser) {
                    {
                        IconButton(onClick = {
                            // TODO: Edycja profilu
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.label_edit_profile),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else null
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.jd),
                    contentDescription = stringResource(R.string.label_profile_picture),
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "${profileViewModel.simpleProfileUser?.firstName ?: ""} ${profileViewModel.simpleProfileUser?.lastName ?: ""}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = stringResource(R.string.label_rating),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "5.0", color = Color.Gray)
                }
            }

            if (!profileViewModel.isCurrentUser) {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (profileViewModel.simpleProfileUser?.friendStatus) {
                        FriendStatus.Friend -> {
                            Button(
                                onClick = { profileViewModel.removeFriend() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            ) {
                                Text(text = stringResource(R.string.label_remove_friend))
                            }
                        }

                        FriendStatus.OutgoingRequest -> {
                            Button(
                                onClick = { profileViewModel.cancelFriendRequest() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            ) {
                                Text(text = stringResource(R.string.label_cancel))
                            }
                        }

                        FriendStatus.IncomingRequest -> {
                            Row {
                                Button(
                                    onClick = { profileViewModel.acceptFriendRequest() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                ) {
                                    Text(text = stringResource(R.string.label_accept_request))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { profileViewModel.rejectFriendRequest() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                ) {
                                    Text(text = stringResource(R.string.label_cancel_request))
                                }
                            }
                        }

                        FriendStatus.Stranger -> {
                            Button(
                                onClick = { profileViewModel.sendFriendRequest() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text(text = stringResource(R.string.label_add_friend))
                            }
                        }

                        null -> {
                            // Obsługa przypadku null, jeśli konieczne
                        }
                    }

                    profileViewModel.friendRequestError?.let { error ->
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }

                    Button(
                        onClick = { /* TODO: Wyślij wiadomość */ },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    }
                }
            }

            if (profileViewModel.isCurrentUser) {
                FloatingTabSwitch(
                    pages = listOf(
                        stringResource(R.string.label_info) to { InfoTab(profileViewModel.fullProfileUser) },
                        stringResource(R.string.label_friends) to { FriendsTab(friendsPagingItems) },
                        stringResource(R.string.label_join_requests) to { JoinRequestsTab() },
                        stringResource(R.string.label_orders) to { CurrentOrdersTab() },
                        stringResource(R.string.label_event_history) to { HistoryTab(eventPagingItems) }
                    )
                )
            }
        }
    }

}


@Composable
fun InfoTab(
    profileUser: UserDTO?
) {
    if(profileUser != null){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profile Picture and Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Picture
                        Image(
                            painter = painterResource(id = R.drawable.jd), // Replace with actual image
                            contentDescription = stringResource(R.string.label_profile_picture),
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        // Name and Roles
                        Column {
                            Text(
                                text = "${profileUser!!.firstName} ${profileUser.lastName}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            profileUser.roles?.let {
                                Text(
                                    text = it.joinToString(", "),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    HorizontalDivider()

                    // Contact Information
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Email
                        profileUser!!.email.let {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                if (it != null) {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }

                        // Phone Number
                        profileUser.phoneNumber?.let {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = it.code+" "+it.number,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        // Birth Date
                        profileUser.birthDate?.let {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Cake,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        // Registered At
                        profileUser.registeredAt?.let {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = formatToDateTime(it, "dd MMM yyyy HH:mm"),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }else{
        MissingPage()
    }
}


@Composable
fun JoinRequestsTab() {
    val context = LocalContext.current

    // Sample events data
    val sampleEvents = listOf(
        Event(
            eventId = 1,
            eventName = "Community Meetup",
            joinRequests = listOf(
                EventDTO.Participant(userId = "user1", firstName = "Alice", lastName = "Smith"),
                EventDTO.Participant(userId = "user2", firstName = "Bob", lastName = "Johnson")
            )
        ),
        Event(
            eventId = 2,
            eventName = "Hackathon",
            joinRequests = listOf(
                EventDTO.Participant(userId = "user3", firstName = "Charlie", lastName = "Brown"),
                EventDTO.Participant(userId = "user4", firstName = "Diana", lastName = "Prince"),
                EventDTO.Participant(userId = "user5", firstName = "Ethan", lastName = "Hunt")
            )
        )
    )

    Spacer(modifier = Modifier.height(64.dp))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, start = 16.dp, end = 16.dp)
    ) {
        items(sampleEvents.size) { id ->
            val event = sampleEvents[id]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Event Name
                    Text(
                        text = event.eventName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    if (event.joinRequests.isNotEmpty()) {
                        event.joinRequests.forEach { participant ->
                            UserListItem(
                                user = participant,
                                showButtons = true,
                                onApproveClick = {
                                    Toast.makeText(
                                        context,
                                        "${participant.firstName} approved for ${event.eventName}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onRejectClick = {
                                    Toast.makeText(
                                        context,
                                        "${participant.firstName} rejected from ${event.eventName}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.label_no_join_requests),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CurrentOrdersTab() {

    // Sample orders data
    val orders = remember {
        mutableStateListOf(
            Order(
                orderId = 1,
                restaurantName = "Pizzeria Bella Italia",
                orderDate = "2023-10-10T19:30:00.000Z",
                items = listOf(
                    OrderItem(name = "Margherita Pizza", quantity = 2),
                    OrderItem(name = "Garlic Bread", quantity = 1)
                ),
                isConfirmed = false
            ),
            Order(
                orderId = 2,
                restaurantName = "Sushi Master",
                orderDate = "2023-10-12T12:00:00.000Z",
                items = listOf(
                    OrderItem(name = "California Roll", quantity = 3),
                    OrderItem(name = "Miso Soup", quantity = 2)
                ),
                isConfirmed = false
            )
        )
    }



    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = stringResource(R.string.label_no_current_orders))
        }
    } else {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, start = 16.dp, end = 16.dp)
        ) {
            items(orders.size) { index ->
                val order = orders[index]
                OrderCard(order = order, onConfirmArrival = {
                    // Simulate confirming arrival
                    orders[index] = order.copy(isConfirmed = true)
                })
            }
        }
    }
}

@Composable
fun HistoryTab(eventPagingItems: LazyPagingItems<EventDTO>?) {

    if (eventPagingItems == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, start = 16.dp, end = 16.dp)
        ) {
            items(eventPagingItems.itemCount) { index ->
                val event = eventPagingItems[index]
                if (event != null) {
                    EventCard(
                        eventName = event.name ?: "",
                        eventDate = event.time,
                        eventLocation = event.restaurant?.name ?: "",
                        interestedCount = event.numberInterested ?: 0,
                        takePartCount = event.numberParticipants ?: 0,
                        onClick = {}
                    )
                }
            }

            eventPagingItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    loadState.append is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    loadState.refresh is LoadState.Error -> {
                        val e = eventPagingItems.loadState.refresh as LoadState.Error
                        item {
                            Text(
                                text = stringResource(R.string.error_loading_events),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        val e = eventPagingItems.loadState.append as LoadState.Error
                        item {
                            Text(
                                text = stringResource(R.string.error_loading_more_events),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FriendsTab(friendsPagingItems: LazyPagingItems<FriendRequestDTO>?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        if (friendsPagingItems == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            item{
                Spacer(
                    modifier = Modifier.height(64.dp)
                )
            }
            items(friendsPagingItems.itemCount) { index ->
                val friend = friendsPagingItems[index]
                val otherUser = friend?.otherUser
                if (otherUser != null) {
                    UserListItem(
                        user = EventDTO.Participant(
                            userId = otherUser.userId,
                            firstName = otherUser.firstName ?: "Unknown",
                            lastName = otherUser.lastName ?: "User"
                        ),
                        showButtons = false
                    )
                }
            }

            friendsPagingItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    loadState.append is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    loadState.refresh is LoadState.Error -> {
                        item {
                            Text(
                                text = stringResource(R.string.error_loading_friends),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        item {
                            Text(
                                text = stringResource(R.string.error_loading_more_friends),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// TODO: BACKEND INSTEAD OF EXAMPLES BELOW
data class Event(
    val eventId: Int,
    val eventName: String,
    val joinRequests: List<EventDTO.Participant>
)

data class OrderItem(
    val name: String,
    val quantity: Int
)

data class Order(
    val orderId: Int,
    val restaurantName: String,
    val orderDate: String,
    val items: List<OrderItem>,
    var isConfirmed: Boolean = false
)

@Composable
fun OrderCard(order: Order, onConfirmArrival: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Restaurant Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Restaurant Image/Icon
                Image(
                    painter = painterResource(id = R.drawable.restaurant_photo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Restaurant Name and Date
                Column {
                    Text(
                        text = order.restaurantName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${stringResource(R.string.label_order_date)}: ${formatToDateTime(order.orderDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Order Items
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                order.items.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "x${item.quantity}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Confirm Arrival Button or Confirmation Text
            if (!order.isConfirmed) {
                Button(
                    onClick = { onConfirmArrival() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = stringResource(R.string.label_confirm_arrival))
                }
            } else {
                Text(
                    text = stringResource(R.string.label_arrival_confirmed),
                    color = Color.Green,
                    modifier = Modifier.align(Alignment.End),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}