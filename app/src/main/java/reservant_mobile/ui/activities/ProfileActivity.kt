package reservant_mobile.ui.activities

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.FriendRequestDTO
import reservant_mobile.data.models.dtos.UserSummaryDTO.FriendStatus
import reservant_mobile.ui.components.EventCard
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.viewmodels.ProfileViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileActivity(navController: NavHostController, userId: String) {
    val profileViewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProfileViewModel(profileUserId = userId) as T
        }
    )

    val friendsPagingItems = profileViewModel.friendsFlow.value?.collectAsLazyPagingItems()

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
                            }
                        }
                        if (profileViewModel.isCurrentUser) {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
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
                    else -> {
                        MissingPage(errorStringId = R.string.error_not_found)
                    }
                }
            }
        }
    }
}


@Composable
fun InfoTab() {
    // TODO: Zakładka o nas
}

@Composable
fun JoinRequestsTab() {
    // TODO: Zakładka prośby o dołączenie do eventów
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
    if (friendsPagingItems == null) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
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


data class Chat(val userName: String, val message: String)
data class Friend(val name: String)
