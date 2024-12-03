package reservant_mobile.ui.activities

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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import reservant_mobile.data.models.dtos.FriendStatus
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.EventCard
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.navigation.UserRoutes
import reservant_mobile.ui.viewmodels.ProfileViewModel
import java.time.LocalDateTime

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

    val ownedEventsFlow by profileViewModel.ownedEventsFlow.collectAsState()
    val ownedEventsPagingItems = ownedEventsFlow?.collectAsLazyPagingItems()

    val visitsFlow by profileViewModel.visitsFlow.collectAsState()
    val visitsPagingItems = visitsFlow?.collectAsLazyPagingItems()

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
        if(!profileViewModel.isLoading){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (profileViewModel.simpleProfileUser != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.jd),
                                    contentDescription = stringResource(R.string.label_profile_picture),
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = "${profileViewModel.simpleProfileUser?.firstName} ${profileViewModel.simpleProfileUser?.lastName}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    profileViewModel.fullProfileUser?.roles?.let {
                                        Text(
                                            text = it.joinToString(", "),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            HorizontalDivider()

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                profileViewModel.fullProfileUser?.email?.let {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Email,
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

                                profileViewModel.fullProfileUser?.phoneNumber?.let { phoneNumber ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${phoneNumber.code} ${phoneNumber.number}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }

                                profileViewModel.simpleProfileUser?.birthDate?.let {
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

                                profileViewModel.fullProfileUser?.registeredAt?.let {
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
                            stringResource(R.string.label_orders) to {
                                CurrentOrdersTab(
                                    visitsPagingItems = visitsPagingItems,
                                    profileViewModel = profileViewModel
                                )
                            },
                            stringResource(R.string.label_join_requests) to {
                                JoinRequestsTab(
                                    ownedEventsPagingItems,
                                    profileViewModel,
                                    navController
                                )
                            },
                            stringResource(R.string.label_friends) to { FriendsTab(friendsPagingItems, navController) },
                            stringResource(R.string.label_event_history) to { HistoryTab(eventPagingItems) }
                        )
                    )
                }
            }
        }else{
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }
    }

}

@Composable
fun JoinRequestsTab(
    ownedEventsPagingItems: LazyPagingItems<EventDTO>?,
    profileViewModel: ProfileViewModel,
    navController: NavHostController
) {
    if (ownedEventsPagingItems == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val listState = rememberLazyListState()
        val currentDateTime = LocalDateTime.now()

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 16.dp, end = 16.dp)
        ) {
            items(ownedEventsPagingItems.itemCount) { index ->
                val event = ownedEventsPagingItems[index]
                if (event != null && LocalDateTime.parse(event.mustJoinUntil).isAfter(currentDateTime)) {

                    profileViewModel.fetchInterestedUsers(event.eventId.toString())
                    val interestedUsersFlow = profileViewModel.getInterestedUsersFlow(event.eventId.toString())
                    val interestedUsersPagingItems = interestedUsersFlow?.collectAsLazyPagingItems()

                    if (interestedUsersPagingItems != null) {
                        when (interestedUsersPagingItems.loadState.refresh) {
                            is LoadState.Loading -> {
                                 //CircularProgressIndicator()
                            }
                            is LoadState.NotLoading -> {
                                if (interestedUsersPagingItems.itemCount > 0) {
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
                                            Text(
                                                text = event.name ?: "",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(8.dp))

                                            interestedUsersPagingItems.itemSnapshotList.items.forEach { participant ->
                                                UserListItem(
                                                    user = participant,
                                                    showButtons = true,
                                                    onApproveClick = {
                                                        profileViewModel.acceptUser(event.eventId.toString(), participant.userId)
                                                    },
                                                    onRejectClick = {
                                                        profileViewModel.rejectUser(event.eventId.toString(), participant.userId)
                                                    },
                                                    onCardClick = {
                                                        navController.navigate(UserRoutes.UserProfile(userId = participant.userId))
                                                    }
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }

                                            interestedUsersPagingItems.apply {
                                                when (loadState.append) {
                                                    is LoadState.Loading -> {
                                                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                                                    }

                                                    is LoadState.Error -> {
                                                        val e = interestedUsersPagingItems.loadState.append as LoadState.Error
                                                        Text(
                                                            text = stringResource(R.string.error_loading_more_requests),
                                                            color = MaterialTheme.colorScheme.error,
                                                            modifier = Modifier.padding(16.dp)
                                                        )
                                                    }

                                                    is LoadState.NotLoading -> null
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            is LoadState.Error -> {
                                val e = interestedUsersPagingItems.loadState.refresh as LoadState.Error
                            }
                        }
                    }
                }
            }

            item {
                if(ownedEventsPagingItems.itemCount == 0){
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(R.string.label_no_join_requests))
                    }
                }
            }

            ownedEventsPagingItems.apply {
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
                        val e = ownedEventsPagingItems.loadState.refresh as LoadState.Error
                        item {
                            Text(
                                text = stringResource(R.string.error_loading_events),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        val e = ownedEventsPagingItems.loadState.append as LoadState.Error
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
fun CurrentOrdersTab(
    visitsPagingItems: LazyPagingItems<VisitDTO>?,
    profileViewModel: ProfileViewModel
) {
    if (visitsPagingItems == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (visitsPagingItems.itemCount == 0) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.label_no_current_orders))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp, start = 16.dp, end = 16.dp)
            ) {
                items(visitsPagingItems.itemCount) { index ->
                    val visit = visitsPagingItems[index]
                    if (visit != null) {
                        OrderCard(
                            visit = visit,
                            onConfirmArrival = {
                                profileViewModel.confirmArrival(visit.visitId.toString())
                            }
                        )
                    }
                }

                visitsPagingItems.apply {
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
                            val e = visitsPagingItems.loadState.refresh as LoadState.Error
                            item {
                                Text(
                                    text = stringResource(R.string.error_loading_visits),
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        loadState.append is LoadState.Error -> {
                            val e = visitsPagingItems.loadState.append as LoadState.Error
                            item {
                                Text(
                                    text = stringResource(R.string.error_loading_more_visits),
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
                .padding(top = 80.dp, start = 16.dp, end = 16.dp)
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
fun FriendsTab(
    friendsPagingItems: LazyPagingItems<FriendRequestDTO>?,
    navController: NavHostController
) {
    if (friendsPagingItems == null) {
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
        ) {

            if (friendsPagingItems.itemCount == 0) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.label_no_friends),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column {

                            for (index in 0 until friendsPagingItems.itemCount) {
                                val friend = friendsPagingItems[index]
                                val otherUser = friend?.otherUser
                                if (otherUser != null) {
                                    Spacer(modifier = Modifier.padding(4.dp))
                                    UserListItem(
                                        user = EventDTO.Participant(
                                            userId = otherUser.userId,
                                            firstName = otherUser.firstName ?: "Unknown",
                                            lastName = otherUser.lastName ?: "User"
                                        ),
                                        showButtons = false,
                                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                                        onCardClick = {
                                            navController.navigate(UserRoutes.UserProfile(userId = otherUser.userId))
                                        }
                                    )
                                    Spacer(modifier = Modifier.padding(4.dp))
                                    if (index < friendsPagingItems.itemCount - 1) {
                                        HorizontalDivider(
                                            thickness = 1.dp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                friendsPagingItems.apply {
                    when (loadState.append) {
                        is LoadState.Loading -> {
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

                        is LoadState.Error -> {
                            val e = friendsPagingItems.loadState.append as LoadState.Error
                            item {
                                Text(
                                    text = stringResource(R.string.error_loading_more_friends),
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        is LoadState.NotLoading -> { /* Do nothing */ }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(visit: VisitDTO, onConfirmArrival: () -> Unit) {
    val isConfirmed = visit.actualStartTime != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        text = visit.restaurant?.name ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${stringResource(R.string.label_visit_date)}: ${visit.date?.let {
                            formatToDateTime(
                                it
                            )
                        }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${stringResource(R.string.label_number_of_guests)}: ${visit.numberOfGuests}",
                    style = MaterialTheme.typography.bodyLarge
                )
                visit.orders?.forEach { order ->
                    Text(
                        text = "${stringResource(R.string.label_order)} #${order.orderId}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!isConfirmed) {
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