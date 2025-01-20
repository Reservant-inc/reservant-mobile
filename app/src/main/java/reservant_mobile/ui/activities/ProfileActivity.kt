package reservant_mobile.ui.activities

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.Regex
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.FriendRequestDTO
import reservant_mobile.data.models.dtos.FriendStatus
import reservant_mobile.data.models.dtos.PhoneNumberDTO
import reservant_mobile.data.models.dtos.ThreadDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.Country
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.CountryPickerView
import reservant_mobile.ui.components.EventCard
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.FormFileInput
import reservant_mobile.ui.components.FormInput
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.LoadedPhotoComponent
import reservant_mobile.ui.components.MyDatePickerDialog
import reservant_mobile.ui.components.ShowErrorToast
import reservant_mobile.ui.navigation.EventRoutes
import reservant_mobile.ui.navigation.UserRoutes
import reservant_mobile.ui.viewmodels.ProfileViewModel
import java.time.LocalDateTime
import java.util.Locale

@Composable
fun ProfileActivity(navController: NavHostController, userId: String) {
    val profileViewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProfileViewModel(profileUserId = userId) as T
        }
    )

    val context = LocalContext.current
    var showErrorToast by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }

    val friendsFlow by profileViewModel.friendsFlow.collectAsState()
    val friendsPagingItems = friendsFlow?.collectAsLazyPagingItems()

    val friendsRequestsFlow by profileViewModel.friendsRequestsFlow.collectAsState()
    val friendsRequestsPagingItems = friendsRequestsFlow?.collectAsLazyPagingItems()

    val eventsFlow by profileViewModel.eventsFlow.collectAsState()
    val eventPagingItems = eventsFlow?.collectAsLazyPagingItems()

    val ownedEventsFlow by profileViewModel.ownedEventsFlow.collectAsState()
    val ownedEventsPagingItems = ownedEventsFlow?.collectAsLazyPagingItems()

    val visitsFlow by profileViewModel.visitsFlow.collectAsState()
    val visitsPagingItems = visitsFlow?.collectAsLazyPagingItems()

    val lazyThreads = profileViewModel.getUserThreads().collectAsLazyPagingItems()

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
                            showEditDialog = true
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

        if(showEditDialog){
            profileViewModel.fullProfileUser?.let { fullUser ->
                EditProfileDialog(
                    viewModel = profileViewModel,
                    user = fullUser,
                    onSave = {
                        profileViewModel.updateProfile(it)
                        showEditDialog = false
                        navController.popBackStack()
                    },
                    onDismiss = { showEditDialog = false },
                    context = context
                )
            }
        }

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
                                LoadedPhotoComponent(
                                    photoModifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    placeholder = R.drawable.unknown_profile_photo,
                                    placeholderModifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)

                                ) {
                                    profileViewModel.simpleProfileUser?.photo?.let {photo ->
                                        profileViewModel.getPhoto(photo)
                                    }
                                }
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
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

                                    if (showErrorToast) {
                                        ShowErrorToast(
                                            context = context,
                                            id = R.string.error_cant_send_message
                                        )
                                        showErrorToast = false
                                    }

                                    Button(
                                        onClick = {
                                            val targetUserId = profileViewModel.simpleProfileUser?.userId ?: return@Button
                                            val myUserId = UserService.UserObject.userId

                                            val threadList = lazyThreads.itemSnapshotList.items
                                            val targetThread = threadList.firstOrNull { thread ->
                                                val participants = thread.participants ?: emptyList()
                                                participants.size == 2 &&
                                                        participants.any { it.userId == targetUserId } &&
                                                        participants.any { it.userId == myUserId }
                                            }

                                            if (targetThread?.threadId != null) {
                                                navController.navigate(
                                                    UserRoutes.Chat(
                                                        threadId = targetThread.threadId,
                                                        threadTitle = targetThread.title!!
                                                    )
                                                )
                                            } else {
                                                profileViewModel.createThreadWithUser(targetUserId) { newThread ->
                                                    if (newThread?.threadId == null) {

                                                        showErrorToast = true
                                                    } else {

                                                        navController.navigate(
                                                            UserRoutes.Chat(
                                                                threadId = newThread.threadId,
                                                                threadTitle = newThread.title!!
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = null,
                                            modifier = Modifier.size(ButtonDefaults.IconSize)
                                        )
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
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { profileViewModel.acceptFriendRequest() },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        ) {
                                            Text(text = stringResource(R.string.label_accept_request))
                                        }

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

                        }
                    }
                }

                if (profileViewModel.isCurrentUser) {
                    FloatingTabSwitch(
                        pages = listOf(
                            stringResource(R.string.label_visits) to {
                                CurrentOrdersTab(
                                    visitsPagingItems = visitsPagingItems
                                )
                            },
                            stringResource(R.string.label_join_requests) to {
                                JoinRequestsTab(
                                    ownedEventsPagingItems,
                                    profileViewModel,
                                    navController
                                )
                            },
                            stringResource(R.string.label_friends) to {
                                FriendsTab(
                                    friendsPagingItems,
                                    friendsRequestsPagingItems,
                                    navController,
                                    profileViewModel
                                )
                            },
                            stringResource(R.string.label_event_history) to {
                                EventHistoryTab(
                                    eventPagingItems,
                                    profileViewModel,
                                    navController
                                )
                            }
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

        var isEmpty by remember { mutableStateOf(true) }

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
                                 CircularProgressIndicator()
                            }
                            is LoadState.NotLoading -> {
                                if (interestedUsersPagingItems.itemCount > 0) {
                                    isEmpty = false
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
                if(isEmpty){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 128.dp),
                        contentAlignment = Alignment.Center
                    ) {
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
    visitsPagingItems: LazyPagingItems<VisitDTO>?
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
                            visit = visit
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
fun EventHistoryTab(
    eventPagingItems: LazyPagingItems<EventDTO>?,
    profileViewModel: ProfileViewModel,
    navController: NavHostController
) {

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

                    var eventPhoto by remember { mutableStateOf<Bitmap?>(null) }

                    LaunchedEffect(event.photo) {
                        event.photo?.let { photo ->
                            eventPhoto = profileViewModel.getPhoto(photo)
                        }
                    }

                    EventCard(
                        eventName = event.name ?: "",
                        eventDate = event.time,
                        eventLocation = event.restaurant?.name ?: "",
                        interestedCount = event.numberInterested ?: 0,
                        takePartCount = event.numberParticipants ?: 0,
                        eventPhoto = eventPhoto,
                        onClick = {
                            navController.navigate(
                                EventRoutes.Details(eventId = event.eventId!!)
                            )
                        }
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
    friendsRequestsPagingItems: LazyPagingItems<FriendRequestDTO>?,
    navController: NavHostController,
    profileViewModel: ProfileViewModel
) {
    Spacer(modifier = Modifier.height(64.dp))

    if (friendsPagingItems == null || friendsRequestsPagingItems == null) {
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
        ) {
            if (friendsRequestsPagingItems.itemCount > 0) {
                item {
                    Text(
                        text = stringResource(R.string.label_friend_requests),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 80.dp, start = 16.dp, end = 16.dp)
                    )
                }

                items(friendsRequestsPagingItems.itemCount) { index ->
                    val request = friendsRequestsPagingItems[index]
                    val otherUser = request?.otherUser
                    if (otherUser != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        UserListItem(
                            user = EventDTO.Participant(
                                userId = otherUser.userId,
                                firstName = otherUser.firstName ?: "Unknown",
                                lastName = otherUser.lastName ?: "User"
                            ),
                            showButtons = true,
                            onApproveClick = {
                                profileViewModel.acceptFriendRequest(otherUser.userId)
                            },
                            onRejectClick = {
                                profileViewModel.rejectFriendRequest(otherUser.userId)
                            },
                            onCardClick = {
                                navController.navigate(
                                    UserRoutes.UserProfile(userId = otherUser.userId)
                                )
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                friendsRequestsPagingItems.apply {
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
                            val e = friendsRequestsPagingItems.loadState.append as LoadState.Error
                            item {
                                Text(
                                    text = stringResource(R.string.error_loading_more_friends),
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        else -> { /* Do nothing */ }
                    }
                }
            }

            println("Friends count: ${friendsPagingItems.itemCount}")

            if (friendsPagingItems.itemCount == 0 && friendsRequestsPagingItems.itemCount == 0) {
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
            } else if(friendsPagingItems.itemCount != 0){
                if(friendsRequestsPagingItems.itemCount == 0){
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }

                item {

                    Text(
                        text = stringResource(R.string.label_friends),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    )
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
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
                                            firstName = otherUser.firstName!!,
                                            lastName = otherUser.lastName!!
                                        ),
                                        showButtons = false,
                                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                                        onCardClick = {
                                            navController.navigate(
                                                UserRoutes.UserProfile(userId = otherUser.userId)
                                            )
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
                        else -> { /* Do nothing */ }
                    }
                }
            }
        }
    }
}


@Composable
fun OrderCard(visit: VisitDTO) {

    val (statusText, statusColor) = when (visit.isAccepted) {
        true -> stringResource(R.string.label_accepted) to Color(58, 148, 16)
        else -> stringResource(R.string.label_pending) to Color(204, 150, 22)
    }

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
                Column {
                    Text(
                        text = visit.restaurant?.name ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${stringResource(R.string.label_visit_date)}: " +
                                (visit.date?.let { formatToDateTime(it) } ?: ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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

            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .clip(RoundedCornerShape(24.dp))
                    .border(BorderStroke(1.dp, statusColor), RoundedCornerShape(24.dp))
                    .background(statusColor.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Status: $statusText",
                    color = statusColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

fun getCountriesList(): List<Country> {
    val phoneNumberUtil = PhoneNumberUtil.getInstance()
    val countries = mutableListOf<Country>()

    for (regionCode in phoneNumberUtil.supportedRegions) {
        val countryCode = phoneNumberUtil.getCountryCodeForRegion(regionCode).toString()
        val countryName = Locale("", regionCode).getDisplayCountry(Locale.ENGLISH)
        countries.add(Country(regionCode.lowercase(Locale.getDefault()), countryCode, countryName))
    }

    return countries.sortedBy { it.fullName }
}
@Composable
fun EditProfileDialog(
    viewModel: ProfileViewModel,
    user: UserDTO,
    onDismiss: () -> Unit,
    onSave: (UserDTO) -> Unit,
    context: Context
) {
    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var phoneNum by remember { mutableStateOf(user.phoneNumber?.number) }
    val countriesList = getCountriesList()
    var mobileCountry by remember {
        mutableStateOf(
            countriesList.firstOrNull { "+${it.code}" == user.phoneNumber?.code }
                ?: countriesList.firstOrNull { it.nameCode == "pl" }
        )
    }

    var birthDate by remember { mutableStateOf(user.birthDate ?: "") }
    var photo by remember { mutableStateOf(user.photo) }
    var formSent by remember { mutableStateOf(false) }

    // Dodajemy stan dla błędu numeru telefonu
    var phoneError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    formSent = true
                    // Sprawdzamy poprawność numeru telefonu przed zapisem
                    phoneError = phoneNum?.let { viewModel.isPhoneInvalid(it) } == true

                    if (!phoneError && firstName.isNotBlank() && lastName.isNotBlank()) {
                        val updatedUser = UserDTO(
                            firstName = firstName,
                            lastName = lastName,
                            birthDate = birthDate,
                            photo = photo,
                            phoneNumber = mobileCountry?.let {
                                PhoneNumberDTO(
                                    code = "+${it.code}",
                                    number = phoneNum ?: ""
                                )
                            }
                        )
                        onSave(updatedUser)
                    }
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
        title = { Text("Edit Profile") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    isError = firstName.isBlank() && formSent,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    isError = lastName.isBlank() && formSent,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                FormInput(
                    inputText = phoneNum ?: "",
                    onValueChange = {
                        phoneNum = it
                        if (formSent) {
                            phoneError = viewModel.isPhoneInvalid(it)
                        }
                    },
                    label = stringResource(R.string.label_phone),
                    leadingIcon = {
                        mobileCountry?.let {
                            CountryPickerView(
                                countries = countriesList,
                                selectedCountry = it,
                                onSelection = { selectedCountry ->
                                    mobileCountry = selectedCountry
                                },
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    optional = true,
                    isError = (formSent && phoneError),
                    errorText = stringResource(R.string.error_register_invalid_phone)
                )

                MyDatePickerDialog(
                    label = "Birth Date",
                    onDateChange = { selectedDate ->
                        birthDate = selectedDate
                    },
                    allowFutureDates = false,
                    startDate = birthDate
                )

                FormFileInput(
                    label = "Profile Photo",
                    onFilePicked = { file ->
                        photo = file.toString()
                    },
                    context = context,
                )
            }
        }
    )
}
