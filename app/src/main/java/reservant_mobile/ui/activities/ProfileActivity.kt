package reservant_mobile.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.reservant_mobile.R
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.data.models.dtos.UserSummaryDTO.FriendStatus
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
                title = { Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if(profileViewModel.isCurrentUser){
                        Text(
                            text = stringResource(R.string.label_my_profile),
                            fontWeight = FontWeight.Bold
                        )
                    }else{
                        Text(
                            text = stringResource(R.string.label_profile),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 48.dp)
                        )
                    }
                } },
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
                            // TODO: Edit profile
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
                                text = "5,00 "+stringResource(R.string.label_rating), // TODO: user rating variable
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
                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(text = stringResource(R.string.label_send_message))
                                }
                            }
                        }
                    }

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

                    if(profileViewModel.isCurrentUser){
                        FloatingTabSwitch(pages = listOf(
                            stringResource(R.string.label_visits) to { VisitsTab() },
                            stringResource(R.string.label_orders) to { OrdersTab() },
                            stringResource(R.string.label_chats) to { ChatsTab() },
                            stringResource(R.string.label_friends) to { FriendsTab() },
                        ))
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
                            EventsContent() // TODO: better events component
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

@Composable
fun VisitsTab() {
    // Content for the Visits tab
}

@Composable
fun OrdersTab() {
    // Content for the Orders tab
}

@Composable
fun ChatsTab() {
    val chats = listOf(
        Chat("John Doe", "Whats up?"),
        Chat("John Doe", "Whats up?"),
        Chat("John Doe", "Whats up?"),
        Chat("John Doe", "Whats up?"),
        Chat("John Doe", "Whats up?"),
        Chat("John Doe", "Whats up?")
    )

    LazyColumn(modifier = Modifier.padding(top = 72.dp)) {
        items(chats) { chat ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = "Chat User Picture",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = chat.userName, fontWeight = FontWeight.Bold)
                    Text(text = chat.message, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun FriendsTab() {
    val friends = listOf(
        Friend("John Doe"),
        Friend("John Doe"),
        Friend("John Doe"),
        Friend("John Doe"),
        Friend("John Doe"),
        Friend("John Doe")
    )

    LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 72.dp)) {
        items(friends) { friend ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.jd),
                    contentDescription = "Friend Profile Picture",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = friend.name, fontWeight = FontWeight.Bold)
            }
        }
    }
}

data class Chat(val userName: String, val message: String)
data class Friend(val name: String)