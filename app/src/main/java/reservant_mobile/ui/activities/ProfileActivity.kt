package reservant_mobile.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import reservant_mobile.data.models.dtos.UserSummaryDTO.FriendStatus
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

    val eventsFlow = profileViewModel.eventsFlow.collectAsState()
    val eventsPagingItems = eventsFlow.value?.collectAsLazyPagingItems()

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                when {
                    profileViewModel.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "5,00", // TODO: Replace with actual user rating
                                        color = Color.Gray
                                    )
                                }
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
                                            // Handle null case if necessary
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
                        }
                    }

                    else -> {
                        MissingPage()
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    placeholder = {
                        Text(
                            stringResource(R.string.label_search)
                        )
                    },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true
                )
            }

            if (profileViewModel.isCurrentUser) {
                item {
                    FloatingTabSwitch(
                        pages = listOf(
                            stringResource(R.string.label_visits) to { VisitsTab() },
                            stringResource(R.string.label_orders) to { OrdersTab() },
                            stringResource(R.string.label_chats) to { ChatsTab() },
                            stringResource(R.string.label_friends) to { FriendsTab() },
                        )
                    )
                }
            } else {
                item {
                    Text(
                        text = stringResource(R.string.label_events),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }

                if (eventsPagingItems == null) {
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
                    items(eventsPagingItems.itemCount) { index ->
                        val event = eventsPagingItems[index]
                        if (event != null) {
                            EventCard(
                                eventCreator = event.creator!!.firstName,
                                eventDate = event.time,
                                eventLocation = event.restaurant!!.address,
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
    }
}

@Composable
fun VisitsTab() {
    // Visits content
}

@Composable
fun OrdersTab() {
    // Orders content
}

@Composable
fun ChatsTab() {
    // Chats content
}

@Composable
fun FriendsTab() {
    // Friends content
}

data class Chat(val userName: String, val message: String)
data class Friend(val name: String)
