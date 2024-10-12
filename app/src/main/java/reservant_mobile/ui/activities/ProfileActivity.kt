package reservant_mobile.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.UserSummaryDTO.FriendStatus
import reservant_mobile.ui.components.EventCard
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileActivity(navController: NavHostController, userId: String) {

    val profileViewModel = viewModel<ProfileViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProfileViewModel(profileUserId = userId) as T
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileViewModel.isCurrentUser) {
                            Text(
                                text = stringResource(R.string.label_my_profile),
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.label_profile),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 48.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.label_back)
                        )
                    }
                },
                actions = {
                    if (profileViewModel.isCurrentUser) {
                        IconButton(onClick = {
                            // TODO: Edycja profilu
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.label_edit_profile)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                profileViewModel.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                profileViewModel.profileUser != null -> {
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
                            text = "${profileViewModel.profileUser!!.firstName} ${profileViewModel.profileUser!!.lastName}",
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
                            profileViewModel.profileUser!!.birthDate?.let {
                                Text(text = it, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "5,00 " + stringResource(R.string.label_rating), // TODO: zmienna z oceną użytkownika
                                color = Color.Gray
                            )
                        }

                        if (!profileViewModel.isCurrentUser) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                when (profileViewModel.profileUser?.friendStatus) {
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
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                        ) {
                                            Text(text = stringResource(R.string.label_cancel_request))
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
                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(text = stringResource(R.string.label_send_message))
                                }
                            }
                        }
                    }

                    if (profileViewModel.isCurrentUser) {
                        FloatingTabSwitch(
                            pages = listOf(
                                stringResource(R.string.label_visits) to { VisitsTab() },
                                stringResource(R.string.label_orders) to { OrdersTab() },
                                stringResource(R.string.label_chats) to { ChatsTab() },
                                stringResource(R.string.label_friends) to { FriendsTab() },
                            )
                        )
                    } else {
                        Column {
                            Text(
                                text = stringResource(R.string.label_events),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(alignment = Alignment.CenterHorizontally)
                            )
                            UserEventsContent(profileViewModel)
                        }
                    }
                } else -> {
                MissingPage(errorStringId = R.string.error_not_found)
            }
            }
        }
    }
}

@Composable
fun UserEventsContent(profileViewModel: ProfileViewModel) {
    val eventsFlow = profileViewModel.eventsFlow.collectAsState()
    val eventsPagingItems = eventsFlow.value?.collectAsLazyPagingItems()

    if (eventsPagingItems == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(eventsPagingItems.itemCount) { index ->
                val event = eventsPagingItems[index]
                if (event != null) {
                    EventCard(
                        eventCreator = event.creatorFullName ?: "",
                        eventDate = event.time ?: "",
                        eventLocation = event.restaurantName ?: "",
                        interestedCount = event.numberInterested ?: 0,
                        takePartCount = event.participants?.size ?: 0
                    )
                }
            }

            eventsPagingItems.apply {
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
                        val e = eventsPagingItems.loadState.refresh as androidx.paging.LoadState.Error
                        item {
                            Text(
                                text = stringResource(R.string.error_loading_events),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    loadState.append is androidx.paging.LoadState.Error -> {
                        val e = eventsPagingItems.loadState.append as androidx.paging.LoadState.Error
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
fun VisitsTab() {
    // Visits tab
}

@Composable
fun OrdersTab() {
    // Orders tab
}

@Composable
fun ChatsTab() {
    // Chats tab
}

@Composable
fun FriendsTab() {
    // Friends tab

data class Chat(val userName: String, val message: String)
data class Friend(val name: String)
