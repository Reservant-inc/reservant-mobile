package reservant_mobile.ui.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import reservant_mobile.data.utils.formatDateTime
import reservant_mobile.ui.navigation.UserRoutes
import reservant_mobile.ui.viewmodels.ChatViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatActivity(navController: NavHostController, userName: String) {
    val chatViewModel: ChatViewModel = viewModel()
    val messagesFlow = chatViewModel.messagesFlow.collectAsState()
    val participantsMap = chatViewModel.participantsMap

    var currentMessage by remember { mutableStateOf(TextFieldValue()) }

    LaunchedEffect(Unit) {
        // Mark all messages as read when entering the chat
        chatViewModel.markMessagesAsRead()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                            ) {
                                // Placeholder for user icon
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = userName)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(UserRoutes.ChatList) }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            val lazyPagingItems = messagesFlow.value?.collectAsLazyPagingItems()

            if (chatViewModel.isLoading){
                Box (
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ){
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    reverseLayout = true // Display the latest messages at the bottom
                ) {
                    lazyPagingItems?.let { pagingItems ->
                        items(count = pagingItems.itemCount) { index ->
                            val message = pagingItems[index]
                            message?.let {
                                val sender = participantsMap[message.authorId]
                                val isSentByMe = message.authorId == chatViewModel.getCurrentUserId()

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start
                                ) {
                                    Column(
                                        horizontalAlignment = if (isSentByMe) Alignment.End else Alignment.Start,
                                        modifier = Modifier.widthIn(max = 300.dp)
                                    ) {
                                        Text(
                                            text = message.contents,
                                            modifier = Modifier
                                                .background(
                                                    if (isSentByMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                                    shape = CircleShape
                                                )
                                                .padding(8.dp),
                                            color = if (isSentByMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Sent by: ${sender?.firstName ?: "Unknown"} ${sender?.lastName ?: "Unknown"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        message.dateSent?.let { date ->
                                            val formattedDate = formatDateTime(date, "dd.MM.yyyy")
                                            val formattedTime = formatDateTime(date, "HH:mm:ss")
                                            Text(
                                                text = "$formattedDate $formattedTime",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = currentMessage,
                    onValueChange = { currentMessage = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .background(Color.LightGray, shape = CircleShape)
                        .padding(16.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.None) // Disable send on enter
                )
                IconButton(
                    onClick = {
                        if (currentMessage.text.isNotBlank() && lazyPagingItems != null) {
                            chatViewModel.createMessage(currentMessage.text) // Send message using ViewModel
                            currentMessage = TextFieldValue()
                        }
                    },
                    enabled = lazyPagingItems != null && !chatViewModel.isLoading // Disable button when chat is not loaded
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}


fun formatDateTime(dateString: String, pattern: String): String {
    return try {
        val formatter = DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 7, true)
            .optionalEnd()
            .toFormatter(Locale.getDefault())

        val dateTime = LocalDateTime.parse(dateString, formatter)
        dateTime.format(DateTimeFormatter.ofPattern(pattern, Locale.getDefault()))
    } catch (e: Exception) {
        "" // Zwróć pusty string w przypadku błędu parsowania
    }

}