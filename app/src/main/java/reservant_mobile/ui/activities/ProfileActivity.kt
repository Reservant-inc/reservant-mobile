package reservant_mobile.ui.activities

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.FriendRequestDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.IconWithHeader
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
                    text = "${profileViewModel.profileUser?.firstName ?: ""} ${profileViewModel.profileUser?.lastName ?: ""}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Cake,
                        contentDescription = stringResource(R.string.label_birthday),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    profileViewModel.profileUser?.birthDate?.let {
                        Text(text = it, color = Color.Gray)
                    }
                }
            }

            if (profileViewModel.isCurrentUser) {
                FloatingTabSwitch(
                    pages = listOf(
                        stringResource(R.string.label_info) to { InfoTab() },
                        stringResource(R.string.label_friends) to { FriendsTab(friendsPagingItems) },
                        stringResource(R.string.label_join_requests) to { JoinRequestsTab() },
                        stringResource(R.string.label_orders) to { CurrentOrdersTab() },
                        stringResource(R.string.label_event_history) to { HistoryTab() }
                    )
                )
            }
        }
    }

}





@Composable
fun InfoTab() {
    val profileUser = UserService.UserObject
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Spacer(modifier = Modifier.height(64.dp))
        // First Name
        Text(
            text = "${stringResource(R.string.name)}: ${profileUser.firstName}",
            style = MaterialTheme.typography.bodyLarge
        )

        // Last Name
        Text(
            text = "${stringResource(R.string.label_lastname)}: ${profileUser.lastName}",
            style = MaterialTheme.typography.bodyLarge
        )

        // Email
//        profileUser.email?.let {
//            Text(
//                text = "${stringResource(R.string.label_email)}: $it",
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }
//
//        // Phone Number
//        profileUser.phoneNumber?.let {
//            Text(
//                text = "${stringResource(R.string.label_phone)}: $it",
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }
//
//        // Birth Date
//        profileUser.birthDate?.let {
//            Text(
//                text = "${stringResource(R.string.label_birthday)}: ${formatToDateTime(it, "dd MMM yyyy")}",
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }
//
//        // Registered At
//        profileUser.registeredAt?.let {
//            Text(
//                text = "${stringResource(R.string.label_registered_at)}: ${formatToDateTime(it, "dd MMM yyyy HH:mm")}",
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }

        // Roles
        Text(
            text = "${stringResource(R.string.label_roles)}: ${profileUser.roles.joinToString(", ")}",
            style = MaterialTheme.typography.bodyLarge
        )
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(sampleEvents.size) { id ->
            val event = sampleEvents[id]
            // Event Header
            Text(
                text = event.eventName,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Join Requests List
            if (event.joinRequests.isNotEmpty()) {
                event.joinRequests.forEach { participant ->
                    UserListItem(
                        user = participant,
                        showButtons = true,
                        onApproveClick = {
                            // Placeholder for approve action
                            Toast.makeText(
                                context,
                                "${participant.firstName} approved for ${event.eventName}",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onRejectClick = {
                            // Placeholder for reject action
                            Toast.makeText(
                                context,
                                "${participant.firstName} rejected from ${event.eventName}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.label_no_join_requests),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun CurrentOrdersTab() {
    // TODO: Zakładka obecnych zamówień z przyciskiem do potwierdzenia przybycia
}

@Composable
fun HistoryTab() {
    // TODO: Zakładka historii eventów
}

@Composable
fun FriendsTab(friendsPagingItems: LazyPagingItems<FriendRequestDTO>?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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

            // Obsługa stanów ładowania
            friendsPagingItems.apply {
                when {
                    loadState.refresh is androidx.paging.LoadState.Loading -> {
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
                    loadState.append is androidx.paging.LoadState.Loading -> {
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
                    loadState.refresh is androidx.paging.LoadState.Error -> {
                        item {
                            Text(
                                text = stringResource(R.string.error_loading_friends),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    loadState.append is androidx.paging.LoadState.Error -> {
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

// TODO: EXAMPLES
data class Event(
    val eventId: Int,
    val eventName: String,
    val joinRequests: List<EventDTO.Participant>
)

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